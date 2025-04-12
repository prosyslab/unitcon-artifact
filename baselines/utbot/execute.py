import csv
import logging
import os
import re
import shutil
import shlex
import math

import sys

from utils import exec_sh

logger = logging.getLogger(__name__)
BIN = "/usr/src/utbot-cli.jar"
DEP_BIN = "/usr/src/utbot-cli-2024.6.jar:/usr/src/deps/lombok.jar"
JAVA_MEM = 8000

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


def remove_expected_exception(filename):
  with open(filename, "r") as f:
    text = f.read()
  instrumented = ""
  expected_p = re.compile("@Test\(expected.*\)")
  cnt = len(expected_p.findall(text))
  instrumented = re.sub(expected_p, "@Test", text)
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
    if fname.endswith("UTBotTest.java"):
      testf = os.path.join(testdir, fname)
  if testf is None:
    logger.error(f"validation failed: {testdir}/UTBotTest.java not found")
    return 0, False

  cnt = read_testnumber(testf)

  validation_log_f = os.path.join(output_dir, "validation_log.txt")
  if os.path.isfile(validation_log_f):
    return cnt, check(config, basedir, validation_log_f)

  throws_n = remove_expected_exception(testf)
  logger.info(f"inserted {throws_n} throw statements to {testf}")

  if not classpaths:
    command = [
      "javac", "-cp", f"{DEP_BIN}:{output_dir}", f"{testdir}/UTBotTest.java"
    ]
  else:
    command = [
      "javac", "-cp", f"{DEP_BIN}:{classpaths}:{output_dir}", f"{testdir}/UTBotTest.java"
    ]
  output = exec_sh(command, os.path.join(basedir, config.project_dir))

  if output is None:
    logger.error(f"validation failed: compile {config.project}")
    return cnt, False

  if not classpaths:
    command = [
      "java", "-cp", f"{DEP_BIN}:{output_dir}"
    ]
  else:
    command = [
      "java", "-cp", f"{DEP_BIN}:{classpaths}:{output_dir}"
    ]
  command.extend(["org.junit.runner.JUnitCore", ".".join([*package_path[:-1], "UTBotTest"])])
  output = exec_sh(command, os.path.join(basedir, config.project_dir), output_error=True, timeout=300)
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

def get_options(config, flags, basedir, classpath, testdir):
  time_budget = flags.timeout * 60 * 1000

  if not config.test_target_path:
    cp = ":".join([os.path.join(basedir, config.project_dir), classpath])
  else:
    cp = ":".join([os.path.join(basedir, config.project_dir), config.test_target_path, classpath])

  options = [
    ("-m", "do-not-mock"),
    ("--test-framework", "junit4"),
    ("--mock-statics", "do-not-mock-statics"),
    ("--generation-timeout", f"{time_budget}"),
    ("-cp", cp),
    ("--source", os.path.join(basedir, config.project_dir, config.test_target_file)),
    ("--project-root", os.path.join(basedir, config.project_dir)),
    ("--language", "Java"),
    ("--output", os.path.join(testdir, "UTBotTest.java"))
  ]
  return options


def execute_utbot(config, basedir, output_dir, flags):
  os.environ["JAVA_HOME"] = "/usr/lib/jvm/java-17-openjdk-amd64"
  os.environ["PATH"] = os.getenv("JAVA_HOME") + "/bin:" + "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin"
  project_dir = os.path.join(basedir, config.project_dir)

  # build check
  project_jar_path = os.path.join(project_dir, "with_dependency.jar")
  if not os.path.isfile(project_jar_path):
    logger.warning(f"Cannot find project jar: {project_jar_path}")
    return {"project": config.project, "status": "project.jar not found"}

  # get classpath setup
  classpath = os.path.relpath(project_jar_path, start=project_dir)
  logger.info(f"classpath: {classpath}")

  ### run utbot
  timelog = os.path.join(output_dir, f"{config.project}-time.txt")
  timeout_sec = flags.timeout * 60
  command = [
      "/usr/bin/time", "-p", "-o", timelog, "java", f"-Xms{JAVA_MEM}m", f"-Xmx{JAVA_MEM}m", "-jar",
      BIN, "generate", config.test_target_class
  ]

  package_path = config.test_target_class.split(".")
  testdir = os.path.join(output_dir, *package_path[:-1])
  if not os.path.isdir(testdir):
      os.makedirs(testdir)

  for op in get_options(config, flags, basedir, classpath, testdir):
    command.extend(list(op))
  
  logger.info(f"execute utbot project: {config.project}")
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