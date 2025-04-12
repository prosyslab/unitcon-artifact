import argparse
import csv
import json
import logging
import os
import pathlib
import sys

from dataclasses import dataclass

logger = logging.getLogger(__name__)


def set_logger(logfile):
    logging.basicConfig(format="%(asctime)s - %(levelname)s - %(name)s - %(message)s",
                        datefmt="%m/%d/%Y %H:%M:%S",
                        level=logging.INFO,
                        handlers=[logging.FileHandler(logfile),
                                  logging.StreamHandler()])


@dataclass
class ProjectConfig:
    project: str
    project_dir: str
    build_bin: str
    source_bench: str
    expected_bug_type: str
    test_target: dict
    test_target_caller: list

    @property
    def test_target_class(self):
        return self.test_target["class"]

    @property
    def test_target_method(self):
        return self.test_target["method"]

    @property
    def test_target_line(self):
        return self.test_target["line"]

    @property
    def test_target_path(self):
        return self.test_target.get("path")

    @property
    def test_target_file(self):
        return self.test_target.get("file")

    @property
    def is_valid(self):
        return self.project_dir and self.test_target_class


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("tool", type=str, help="[evosuite | randoop | evofuzz | npetest | utbot | all]")
    parser.add_argument("target",
                        type=str,
                        help="[target project name | subset(Javac, Maven) | all | minimal]")
    parser.add_argument("--tools-home", type=pathlib.Path, default=None)
    parser.add_argument("--projects", type=argparse.FileType('r'), default="projects.json")
    parser.add_argument("--minimal_projects", type=argparse.FileType('r'), default="minimal.json")
    parser.add_argument("--benchmarks", type=pathlib.Path, default=None)
    parser.add_argument("--results", type=pathlib.Path, default="results")
    parser.add_argument("--log", type=pathlib.Path, default="execution_log.txt")
    parser.add_argument("--force", action="store_true", help="remove cached results")
    parser.add_argument("--seed", type=int, default=1234, help="random seed")
    parser.add_argument("--timeout", type=int, default=30, help="timeout (minutes)")
    parser.add_argument("--target_method", action="store_true")
    args = parser.parse_args()

    if not args.results.is_dir():
        os.makedirs(args.results)

    set_logger(args.log.absolute().as_posix())

    if args.tools_home:
        tools_home = args.tools_home.absolute().as_posix()
    else:
        tools_home = os.path.abspath(os.getenv("TOOLS_HOME"))
    assert os.path.isdir(tools_home), "check the option --tools-home or env $TOOLS_HOME"
    sys.path.append(tools_home)
    from evosuite.execute import execute_evosuite
    from randoop.execute import execute_randoop
    from evofuzz.execute import execute_evofuzz
    from npetest.execute import execute_npetest
    from utbot.execute import execute_utbot

    tool = args.tool.lower()

    if args.benchmarks:
        benchmarks_basedir = args.benchmarks.absolute().as_posix()
    else:
        benchmarks_basedir = os.path.abspath(os.getenv("BENCH_HOME"))
    assert os.path.isdir(benchmarks_basedir), "check the option --benchmarks"

    target = args.target.lower()
    projects = json.load(args.projects)
    projects = [ProjectConfig(**p) for p in projects]
    if target == "all":
        pass
    elif target == "minimal":
        projects = json.load(args.minimal_projects)
        projects = [ProjectConfig(**p) for p in projects]
    elif target in ["javac", "maven"]:
        projects = [p for p in projects if p.project_dir.lower().startswith(target)]
    else:
        projects = [p for p in projects if p.project.lower() == target]
    assert len(projects) > 0, "check the [target] argument"

    projects = [p for p in projects if p.is_valid]
    assert len(projects) > 0, "no valid project to execute tools"

    results_dir = args.results.absolute().as_posix()
    randoop_reports = list()
    evosuite_reports = list()
    evofuzz_reports = list()
    npetest_reports = list()
    utbot_reports = list()
    for config in projects:
        if tool in ["randoop", "all"]:
            dirname = "randoop"
            if args.target_method:
                dirname = "randoop-method"
            output_dir = os.path.join(results_dir, dirname, config.project)
            report = execute_randoop(config, benchmarks_basedir, output_dir, args)
            if report:
                randoop_reports.append(report)
        if tool in ["evosuite", "all"]:
            dirname = "evosuite"
            if args.target_method:
                dirname = "evosuite-method" 
            output_dir = os.path.join(results_dir, dirname, config.project)
            report = execute_evosuite(config, benchmarks_basedir, output_dir, args)
            if report:
                evosuite_reports.append(report)
        if tool in ["evofuzz", "all"]:
            dirname = "evofuzz"
            if args.target_method:
                dirname = "evofuzz-method" 
            output_dir = os.path.join(results_dir, dirname, config.project)
            report = execute_evofuzz(config, benchmarks_basedir, output_dir, args)
            if report:
                evofuzz_reports.append(report)
        if tool in ["npetest", "all"]:
            dirname = "npetest"
            if args.target_method:
                dirname = "npetest-method" 
            output_dir = os.path.join(results_dir, dirname, config.project)
            report = execute_npetest(config, benchmarks_basedir, output_dir, args)
            if report:
                npetest_reports.append(report)
        if tool in ["utbot"]:
            output_dir = os.path.join(results_dir, "utbot", config.project)
            report = execute_utbot(config, benchmarks_basedir, output_dir, args)
            if report:
                utbot_reports.append(report)

    file = "randoop.csv"
    if args.target_method:
        file = "randoop-method.csv"
    reportfile = os.path.join(results_dir, file)
    fieldnames = ["project", "status", "n_tests", "validated"]
    dump_results(reportfile, fieldnames, randoop_reports)

    file = "evosuite.csv"
    if args.target_method:
        file = "evosuite-method.csv"
    reportfile = os.path.join(results_dir, file)
    fieldnames = ["project", "status", "n_tests", "validated"]
    dump_results(reportfile, fieldnames, evosuite_reports)

    file = "evofuzz.csv"
    if args.target_method:
        file = "evofuzz-method.csv"
    reportfile = os.path.join(results_dir, file)
    fieldnames = ["project", "status", "n_tests", "validated"]
    dump_results(reportfile, fieldnames, evofuzz_reports)
    
    file = "npetest.csv"
    if args.target_method:
        file = "npetest-method.csv"
    reportfile = os.path.join(results_dir, file)
    fieldnames = ["project", "status", "n_tests", "validated"]
    dump_results(reportfile, fieldnames, npetest_reports)

    reportfile = os.path.join(results_dir, "utbot.csv")
    fieldnames = ["project", "status", "n_tests", "validated"]
    dump_results(reportfile, fieldnames, utbot_reports)


def dump_results(reportfile, fieldnames, reports):
    if not reports:
        return
    mode = "a" if os.path.isfile(reportfile) else "w"
    with open(reportfile, mode) as f:
        w = csv.DictWriter(f, fieldnames=fieldnames)
        if mode == "w":
            w.writeheader()
        for r in reports:
            w.writerow(r)


if __name__ == "__main__":
    main()
