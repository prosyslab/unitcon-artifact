import argparse
import os
import json
import subprocess
import logging
import pathlib

import jar_maker as jm

logger = logging.getLogger(__name__)


def set_logger(logfile):
    logging.basicConfig(format="%(asctime)s - %(levelname)s - %(name)s - %(message)s",
                        datefmt="%m/%d/%Y %H:%M:%S",
                        level=logging.INFO,
                        handlers=[logging.FileHandler(logfile),
                                  logging.StreamHandler()])


def build(benchmark_dir, project_info):
    name = project_info["project"]
    logger.info("**********")
    logger.info(f"build started: {name}")

    project_dir = os.path.join(benchmark_dir, project_info["project_dir"])

    output = jm.execute_build_cmd(project_dir)
    logger.info(f"build log:\n{output}")
    logger.info(f"build done: {name}")

    output = jm.make_jar_with_dependencies(project_dir)
    logger.info(f"gather log:\n{output}")
    logger.info(f"gather with_dependency.jar done: {name}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("target",
                        type=str,
                        help="[target project name | subset(Maven, Javac) | all | minimal]")
    parser.add_argument("--projects", type=argparse.FileType('r'), default="projects.json")
    parser.add_argument("--minimal_projects", type=argparse.FileType('r'), default="minimal.json")
    parser.add_argument("--benchmarks", type=pathlib.Path, default=None)
    parser.add_argument("--log", type=pathlib.Path, default="build_logs.txt")
    args = parser.parse_args()

    set_logger(args.log.absolute().as_posix())

    if args.benchmarks:
        benchmarks_basedir = args.benchmarks.absolute().as_posix()
    else:
        benchmarks_basedir = os.path.abspath(os.getenv("BENCH_HOME"))
    assert os.path.isdir(benchmarks_basedir), "check the option --benchmarks"

    target = args.target.lower()
    projects = json.load(args.projects)
    if target == "all":
        pass
    elif target == "minimal":
        projects = json.load(args.minimal_projects)
        projects = [p for p in projects]
    elif target in ["maven", "javac"]:
        projects = [p for p in projects if p["project_dir"].lower().startswith(target)]
    else:
        projects = [p for p in projects if p["project"].lower() == target]
    assert len(projects) > 0, "check the [target] argument"

    for project_info in projects:
        build(benchmarks_basedir, project_info)
