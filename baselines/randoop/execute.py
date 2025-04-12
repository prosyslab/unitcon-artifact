import logging
import os
import re
import shutil
import shlex

import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.realpath(__file__))))

from utils import exec_sh

logger = logging.getLogger(__name__)
BIN = os.path.join(os.path.dirname(__file__), "randoop-4.3.2", "randoop-all-4.3.2.jar")


def validate(config, basedir, output_dir, classpaths):
    testf = os.path.join(output_dir, "ErrorTest.java")
    if not os.path.isfile(testf):
        logger.error(f"validation failed: {testf} not found")
        return False

    validation_log_f = os.path.join(output_dir, "validation_log.txt")
    if os.path.isfile(validation_log_f):
        return check(config, basedir, validation_log_f)

    command = ["javac", "-cp", f"{classpaths}:{BIN}", f"{output_dir}/*.java"]
    output = exec_sh(command, os.path.join(basedir, config.project_dir))
    if output is None:
        logger.error(f"validation failed: compile {config.project}")
        return False

    command = [
        "java", "-cp", f"{classpaths}:{BIN}:{output_dir}", "org.junit.runner.JUnitCore"
    ]
    p = re.compile("ErrorTest(\d+).class")
    test_targets = []
    for fname in os.listdir(output_dir):
        if re.match(p, fname):
            test_targets.append(fname[:-6])
    batch_size = 5
    output = ""
    for start in range(0, len(test_targets), batch_size):
        end = min(start + batch_size, len(test_targets))
        curr_command = command + test_targets[start:end]
        output += exec_sh(curr_command, os.path.join(basedir, config.project_dir), output_error=True)
    with open(validation_log_f, "w") as f:
        f.write(output)
    return check(config, basedir, validation_log_f)


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


def execute_randoop(config, basedir, output_dir, flags):
    # reset previous results if exists
    if os.path.isdir(output_dir):
        if not flags.force:
            logger.warning(f"skip: {output_dir} exists")
            return summarize(config, basedir, output_dir)
        shutil.rmtree(output_dir)
        logger.info(f"reset: {output_dir} is removed")
    os.makedirs(output_dir)

    # build check
    project_dir = os.path.join(basedir, config.project_dir)
    project_jar_path = os.path.join(project_dir, "unitcon-out", "with-dependency.jar")
    if not os.path.isfile(project_jar_path):
        logger.warning(f"Cannot find project jar: {project_jar_path}")
        return { "project": config.project, "status": "project.jar not found" }

    # get classpaths setup
    classpath = os.path.relpath(project_jar_path, start=project_dir)
    logger.info(f"classpath: {classpath}")

    ### run randoop
    timelog = os.path.join(output_dir, f"{config.project}-time.txt")
    timeout_sec = flags.timeout * 60
    command = [
        "ulimit", "-s", "65536;", "/usr/bin/time", "-p", "-o", timelog, "java", "-Xms8000m",
        "-Xmx8000m", "-cp", f"{classpath}:{BIN}", "randoop.main.Main", "gentests",
        f"--testclass={config.test_target_class}", f"--time-limit={timeout_sec}",
        "--flaky-test-behavior=DISCARD", "--no-error-revealing-tests=false",
        "--no-regression-tests=true", "--check-compilable=true", "--npe-on-null-input=ERROR",
        "--npe-on-non-null-input=ERROR", f"--randomseed={flags.seed}",
        f"--junit-output-dir={output_dir}"
    ]
    if flags.target_method:
        methodlist_f = os.path.join(output_dir, "method-list.txt")
        with open(methodlist_f, "w") as f:
            sig = convert_jni_signature(config.test_target_class, config.test_target_method)
            f.write(sig + "\n")
            for signature in config.test_target_caller:
                f.write(convert_jni_signature(signature["class"], signature["method"]) + "\n")
        command.append(f"--methodlist={methodlist_f}")

    logger.info(f"execute randoop project: {config.project}")
    output = exec_sh(command, project_dir, output_error=True, timeout=5*timeout_sec)
    with open(os.path.join(output_dir, "execution_log.txt"), "w") as f:
        f.write(output or "failed")
    return summarize(config, basedir, output_dir, classpath)


def summarize(config, basedir, output_dir, classpath=None):
    def fail():
        logger.error(f"failed: {config.project}")
        return {"project": config.project, "status": "failed", "n_tests": 0, "validated": False}

    execution_log_f = os.path.join(output_dir, "execution_log.txt")
    if not os.path.isfile(execution_log_f):
        return fail()

    with open(execution_log_f, "r") as f:
        output = f.read()
    if not output or output == "failed":
        return fail()

    p = re.compile("Error-revealing test count: (\d+)")
    if p.search(output):
        logger.info(f"done: {config.project}")
        cnt = int(p.search(output).group(1))
        success = validate(config, basedir, output_dir, classpath)
        logger.info(f"validated: {success}")
        return {"project": config.project, "status": "done", "n_tests": cnt, "validated": success}
    if "No error-revealing tests to output" in output:
        logger.info(f"done: {config.project}")
        logger.info("validated: False")
        return {"project": config.project, "status": "done", "n_tests": 0, "validated": False}

    return fail()


### parse & convert jni signature
def convert_params(params, array=0):
    if not params:
        return []
    if params[0] == "[":
        return convert_params(params[1:], array=array + 1)

    basic_types = {
        "V": "void",
        "Z": "boolean",
        "I": "int",
        "J": "long",
        "D": "double",
        "F": "float",
        "B": "byte",
        "C": "char",
        "S": "short",
    }
    if params[0] in basic_types:
        typ = basic_types[params[0]]
        rest = params[1:]
    else:
        assert ";" in params and params.startswith("L"), params
        eidx = params.index(";")
        typ = params[1:eidx].replace("/", ".")
        rest = params[eidx + 1:]
    for i in range(array):
        typ = typ + "[]"
    return [typ] + convert_params(rest)


def convert_jni_signature(classname, methodname):
    lbr = methodname.index("(")
    rbr = methodname.index(")")
    params = convert_params(methodname[lbr + 1:rbr])
    params = ",".join(params)
    return f"{classname}.{methodname[:lbr]}({params})"
