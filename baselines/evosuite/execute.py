import csv
import logging
import os
import re
import shutil
import shlex
import math

import sys

sys.path.append(os.path.dirname(os.path.dirname(os.path.realpath(__file__))))

from utils import exec_sh

logger = logging.getLogger(__name__)
BIN = os.path.join(os.path.dirname(__file__), "evosuite-1.2.0.jar")
JAVA_MEM = 8000


def reset_evosuite_results(dirpaths, force):
    exists = [path for path in dirpaths if os.path.isdir(path)]
    if len(exists) > 0 and not force:
        return
    for path in exists:
        shutil.rmtree(path)
        logger.info(f"reset: {path} is removed")
    for path in dirpaths:
        os.makedirs(path)
    return dirpaths


def get_idx(text):
    p = re.compile(r"public void test(\d+)\(")
    m = re.match(p, text)
    if not m:
        return -1
    if not m.groups():
        return -1
    try:
        return int(m.groups()[0])
    except:
        return -1


def read_testnumber(filename):
    with open(filename, "r") as f:
        lines = [l.strip() for l in f.readlines() if "public void test" in l]
    if not lines:
        return 0
    indices = [get_idx(l) for l in lines]
    return max(indices) + 1


def filter_comments(text):
    lines = [l for l in text.splitlines(keepends=True) if not l.strip().startswith("//")]
    return "".join(lines)


def insert_throw_statements(filename, expected_type):
    with open(filename, "r") as f:
        text = f.read()
    text = filter_comments(text)
    cnt = 0
    instrumented = ""
    exception_type = expected_type.split(".")[-1]
    target = f"catch({exception_type} e)"
    while target in text:
        idx = text.index(target)
        idx = text.index("{", idx + 1) + 1
        instrumented += text[:idx]
        text = text[idx:]
        close_idx = text.index("}")
        while text[:close_idx].count("{") > text[:close_idx].count("}"):
            close_idx = text.index("}", close_idx + 1)
        instrumented += text[:close_idx]
        if not text[:close_idx].strip().endswith("throw e;"):
            instrumented += "throw e; "
        text = text[close_idx:]
        cnt += 1
    instrumented += text
    with open(filename, "w") as f:
        f.write("".join(instrumented))
    return cnt


def validate(config, basedir, output_dir, classpaths):
    package_path = config.test_target_class.split(".")
    testdir = os.path.join(output_dir, *package_path[:-1])
    if not os.path.isdir(testdir):
        logger.error(f"validation failed: {testdir} not found")
        return 0, False

    testf = None
    for fname in os.listdir(testdir):
        if fname.endswith("_ESTest.java"):
            testf = os.path.join(testdir, fname)
    if testf is None:
        logger.error(f"validation failed: {testdir}/*_ESTest.java not found")
        return 0, False

    cnt = read_testnumber(testf)

    validation_log_f = os.path.join(output_dir, "validation_log.txt")
    if os.path.isfile(validation_log_f):
        return cnt, check(config, basedir, validation_log_f)

    throws_n = insert_throw_statements(testf, config.expected_bug_type)
    logger.info(f"inserted {throws_n} throw statements to {testf}")

    if not classpaths:
        command = [
            "source", "evosuite-files/evosuite.properties;", "javac", "-cp", f"$CP:{BIN}",
            f"{testdir}/*.java"
        ]
    else:
        command = ["javac", "-cp", f"{classpaths}:{BIN}", f"{testdir}/*.java"]
    output = exec_sh(command, os.path.join(basedir, config.project_dir))
    if output is None:
        logger.error(f"validation failed: compile {config.project}")
        return cnt, False

    if not classpaths:
        command = [
            "source", "evosuite-files/evosuite.properties;", "java", "-cp", f"$CP:{BIN}:{output_dir}"
        ]
    else:
        command = ["java", "-cp", f"{classpaths}:{BIN}:{output_dir}"]
    command.extend(["org.junit.runner.JUnitCore", f"{config.test_target_class}_ESTest"])
    output = exec_sh(command,
                     os.path.join(basedir, config.project_dir),
                     output_error=True,
                     timeout=300)
    with open(validation_log_f, "w") as f:
        f.write(output)
    return cnt, check(config, basedir, validation_log_f)


def check(config, basedir, validation_log_f):
    expectedf = os.path.join(basedir, config.project_dir, "unitcon-properties", "expected-bug")
    if not os.path.isfile(expectedf):
        logger.error(f"validation failed: {expectedf} not found")
        return False

    with open(expectedf, "r") as f:
        expected_traces = [l.strip() for l in f.readlines()]

    with open(validation_log_f, "r") as f:
        output = f.read()

    if not all(l in output for l in expected_traces):
        return False

    idx = min([output.index(l) for l in expected_traces])
    before_trace = output[:idx].strip().splitlines()[-1].strip()
    if not before_trace.startswith(config.expected_bug_type):
        return False
    return True


def get_options(config, flags, classpath, output_dir):
    options = [("-mem", str(JAVA_MEM)), ("-class", config.test_target_class),
               ("-seed", str(flags.seed)), ("-projectCP", classpath)]
    if config.test_target_path:
        options.append(("-target", config.test_target_path))

    timeout_sec = flags.timeout * 60
    covered_goals_path = os.path.join(output_dir, "covered_goals.txt")
    doptions = [("-Dsearch_budget", str(timeout_sec)), ("-Dglobal_timeout", str(timeout_sec + 60)),
                ("-Dstrategy", "MOSuite"), ("-Dalgorithm", "DynaMOSA"), ("-Dshow_progress", "false"),
                ("-Dignore_missing_statistics", "true"), ("-Dwrite_covered_goals_file", "true"),
                ("-Dtest_dir", output_dir), ("-Dreport_dir", output_dir),
                ("-Dcovered_goals_file", covered_goals_path)]
    doptions += [("-Dfunctional_mocking_percent", "0.0"),
                    ("-Dfunctional_mocking_input_limit", "0"), ("-Dmock_if_no_generator", "false"),
                    ("-Dp_functional_mocking", "0"), ("-Dvirtual_fs", "false"),
                    ("-Dvirtual_net", "false"), ("-Dreplace_calls", "false"),
                    ("-Dreplace_gui", "false")]
    if flags.target_method and config.test_target_caller:
        methods = [config.test_target_method]
        for signature in config.test_target_caller:
            methods.append("{class}.{method}".format(**signature))
        method_list = ":".join(methods)
        doptions += [("-Dtarget_method_list", shlex.quote(method_list))]
    elif flags.target_method:
        doptions += [("-Dtarget_method", shlex.quote(config.test_target_method))]
    return options + doptions


def execute_evosuite(config, basedir, output_dir, flags):
    # reset previous results if exists
    project_dir = os.path.join(basedir, config.project_dir)
    if not reset_evosuite_results([output_dir], flags.force):
        logger.warning(f"skip: {output_dir} exists")
        return summarize(config, basedir, output_dir)

    # build check
    project_jar_path = os.path.join(project_dir, "unitcon-out", "with-dependency.jar")
    if not os.path.isfile(project_jar_path):
        logger.warning(f"Cannot find project jar: {project_jar_path}")
        return {"project": config.project, "status": "project.jar not found"}

    # get classpath setup
    classpath = os.path.relpath(project_jar_path, start=project_dir)
    logger.info(f"classpath: {classpath}")

    ### run evosuite
    timelog = os.path.join(output_dir, f"{config.project}-time.txt")
    timeout_sec = flags.timeout * 60
    command = [
        "/usr/bin/time", "-p", "-o", timelog, "java", f"-Xms{JAVA_MEM}m", f"-Xmx{JAVA_MEM}m", "-jar",
        BIN
    ]

    for op in get_options(config, flags, classpath, output_dir):
        command.extend(list(op))

    logger.info(f"execute evosuite project: {config.project}")
    output = exec_sh(command, project_dir, output_error=True, timeout=5 * timeout_sec)
    with open(os.path.join(output_dir, "execution_log.txt"), "w") as f:
        f.write(output or "failed")
    return summarize(config, basedir, output_dir, classpath)


def summarize(config, basedir, output_dir, classpath=None):
    def fail():
        logger.error(f"failed: {config.project}")
        return {"project": config.project, "status": "failed"}

    execution_log_f = os.path.join(output_dir, "execution_log.txt")
    if not os.path.isfile(execution_log_f):
        return fail()

    with open(execution_log_f, "r") as f:
        output = f.read().strip()
    if not output or output == "failed":
        return fail()

    logger.info(f"done: {config.project}")
    cnt, success = validate(config, basedir, output_dir, classpath)
    logger.info(f"validated: {success}")
    return {
        "project": config.project,
        "status": "done",
        "n_tests": cnt,
        "validated": success
    }
