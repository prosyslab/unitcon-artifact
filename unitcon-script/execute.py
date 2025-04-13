import argparse
import os
import pathlib
import shutil
import subprocess
import csv
import re
import json

unitcon_home = os.path.abspath(os.getenv("UNITCON_TOOL_HOME"))
bench_home = os.path.abspath(os.getenv("BENCH_HOME"))

def build(p):
    unitcon_path = os.path.join(unitcon_home, "unitcon")
    cmd = " ".join([unitcon_path, "build", p])
    ret = 0
    try:
        output = subprocess.check_output(cmd, cwd=p, stderr=subprocess.STDOUT, shell=True)
        ret = 0
        return ret
    except subprocess.CalledProcessError as ex:
        output = ex.output
        print("Error!", p)
        ret = 1
        return ret


def combine_analysis_command(p_dir_name, p, proj_infos):
    unitcon_path = os.path.join(unitcon_home, "unitcon")
    cmd = " ".join([unitcon_path, "analyze", p])
    value = proj_infos.get(p_dir_name, None)
    if value != None:
        cmd = " ".join([cmd, "--target", value["target_source"] + ":" + str(value["target_line"])])
        if value["keep_going"]:
            cmd = " ".join([cmd, "--keep-going"])
        if value["interproc"]:
            cmd = " ".join([cmd, "--interproc"])
        if value["skip_procedures"]:
            cmd = " ".join([cmd, "--skip-procedures", value["skip_procedures"]])
    return cmd


def analyze(p_dir_name, p, proj_infos):
    cmd = combine_analysis_command(p_dir_name, p, proj_infos)
    ret = 0
    try:
        output = subprocess.check_output(cmd, cwd=p, stderr=subprocess.STDOUT, shell=True)
        ret = 0
        return ret
    except subprocess.CalledProcessError as ex:
        output = ex.output
        print("Error!", p)
        ret = 1
        return ret

def combine_synthesis_command(p_dir_name, p, syn_mode, time_out, proj_infos):
    unitcon_path = os.path.join(unitcon_home, "unitcon")
    cmd = " ".join([unitcon_path, "synthesize", p, syn_mode, "--time-out", str(time_out)])
    value = proj_infos.get(p_dir_name, None)

    if value != None:
        cmd = " ".join([cmd, "--target", value["target_source"] + ":" + str(value["target_line"])])
    return cmd


def run(p_dir_name, p, mode, time_out, proj_infos, log_file):
    assert (mode == "basic" or mode == "prune" or mode == "priority"
            or mode == "full"), f"Undefined Run Mode: {mode}"

    syn_mode = ""
    if mode == "basic":
        syn_mode = "--basic-mode"
    elif mode == "prune":
        syn_mode = "--pruning-mode"
    elif mode == "priority":
        syn_mode = "--priority-mode"
    else:
        syn_mode = ""

    cmd = combine_synthesis_command(p_dir_name, p, syn_mode, time_out, proj_infos)
    ret = 0
    with open(log_file, "a") as f:
        f.write("Start: " + p + "\n")
    try:
        output = subprocess.check_output(cmd, cwd=p, stderr=subprocess.STDOUT, shell=True)
        ret = 0
    except subprocess.CalledProcessError as ex:
        output = ex.output
        ret = 1
        print("Error!", p)
    with open(log_file, "a") as f:
        f.write("End: " + p + "\n")
    return ret


def success_checker(p, p_name):
    log_path = os.path.join(p, "unitcon-out", "log.txt")
    tcdir_path = os.path.join(p, "unitcon-out", "unitcon-tests")

    lines = ""

    with open(log_path, 'r') as f:
        lines = f.read()

    tc_p = re.compile('# of test files: (\d+)')
    multi_tc_p = re.compile('# of multi-test: (\d+)')

    total_p = re.compile('.*total time: (\d+.\d+)')
    nsucc_p = re.compile('End UnitCon .*\((\d+)\)')
    first_tc_p = re.compile('First Success Test: (UnitconTest\d+)')
    total_exec_p = re.compile('Total Multi-Executions: (\d+)')

    nmultc = 0
    ntc = 0
    num_success = 0
    total_time = 0.0

    if lines and tc_p.search(lines):
        ntcs = tc_p.findall(lines)
        ntc = int(ntcs[-1])
    if lines and multi_tc_p.search(lines):
        nmultcs = multi_tc_p.findall(lines)
        nmultc = int(nmultcs[-1])
    if lines and total_p.search(lines):
        total_time = float((total_p.search(lines).group(1)))
    if lines and nsucc_p.search(lines):
        num_success = int(nsucc_p.search(lines).group(1))

    validated = False
    if num_success > 0: validated = True

    first_tc = ""
    succ_time = 0.0
    executions = 0

    if lines and first_tc_p.search(lines):
        first_tc = first_tc_p.search(lines).group(1)
    if lines and total_exec_p.search(lines):
        executions = total_exec_p.search(lines).group(1)


    succ_time_p = re.compile('.*Duration of synthesis: (\d+.\d+)')

    if first_tc != "":
        tc_content = ""
        with open(os.path.join(tcdir_path, first_tc + ".java"), 'r') as f:
            tc_content = f.read()

        if tc_content and succ_time_p.search(tc_content):
            succ_time = float(succ_time_p.search(tc_content).group(1))


    return {
        "project": p_name,
        "num_of_tcs": ntc,
        "num_of_multcs": nmultc,
        "num_of_multi_execs": executions,
        "succ_time": succ_time,
        "fst_succ_tc": first_tc,
        "validated": validated,
        "total_time": total_time
    }


def copy(report_path, projects):
    for (p, path) in projects:
        t_path = os.path.join(path, "unitcon-out", "unitcon-tests")
        log_path = os.path.join(path, "unitcon-out", "log.txt")
        shutil.copytree(t_path, os.path.join(report_path, p))
        shutil.copyfile(log_path, os.path.join(report_path, p, "log.txt"))


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("run",
                        type=str,
                        default="synthesize",
                        help='[ build | analyze | synthesize ]')
    parser.add_argument("target", type=str, help='[ target project name | all | minimal ]')
    parser.add_argument("--analysis_info", type=argparse.FileType('r'), default="script/projects.json")
    parser.add_argument("--minimal_projects", type=argparse.FileType('r'), default="script/minimal.json")
    parser.add_argument("--mode", type=str, default="", help='[ basic | prune | priority | full ]')
    parser.add_argument("--report", type=str, default="")
    parser.add_argument("--time_out", type=int, default=600, help='time out (s)')
    args = parser.parse_args()

    target_projects = list()
    if args.target == "all":
        pass
    elif args.target == "minimal":
        target_projects = json.load(args.minimal_projects)
    else:
        target_projects.append(args.target)

    projects = list()
    for p in os.listdir(os.path.join(bench_home, "Maven")):
        if not target_projects or p in target_projects:
            path = os.path.join(bench_home, "Maven", p)
            projects.append((p, path))
    for p in os.listdir(os.path.join(bench_home, "Javac")):
        if p == "deps":
            continue
        if not target_projects or p in target_projects:
            path = os.path.join(bench_home, "Javac", p)
            projects.append((p, path))

    assert len(projects) > 0, "check the [target] argument"
        
    if args.run == "build":
        for (p, path) in projects:
            print(f"build started: {p}")
            build(path)
            print(f"build done: {p}")
            
        print("all build done!")

    elif args.run == 'analyze':
        all_projects = json.load(args.analysis_info)
        for (p, path) in projects:
            print(f"analysis started: {p}")
            analyze(p, path, all_projects)
            print(f"analysis done: {p}")
            
        print("all analysis done!")

    elif args.run == "synthesize":
        report_path = os.path.join(os.getenv("UNITCON_HOME"), "results", args.report)
        if not os.path.isdir(report_path):
            os.makedirs(report_path)
        unitcon_output = os.path.join(report_path, "unitcon-log.txt")

        all_projects = json.load(args.analysis_info)
        reports = list()

        for (p, path) in projects:
            print(f"synthesis started: {p}")
            run(p, path, args.mode, args.time_out, all_projects, unitcon_output)
            print(f"synthesis done: {p}")
            report = success_checker(path, p)
            reports.append(report)
            
        print("all synthesis done!")

        report_file = os.path.join(report_path, "unitcon-report.csv")

        if os.path.isfile(report_file):
            mode = "a"
        else:
            mode = "w"

        fieldnames = [
            "project", "num_of_tcs", "num_of_multcs", "num_of_multi_execs", "succ_time", "fst_succ_tc", "validated",
            "total_time"
        ]
        with open(report_file, mode) as f:
            w = csv.DictWriter(f, fieldnames=fieldnames)
            if mode == "w":
                w.writeheader()
            for r in reports:
                w.writerow(r)

        copy(report_path, projects)
    else:
        print(f"Undefined Undefined Run Mode: {args.run}")


if __name__ == "__main__":
    main()
