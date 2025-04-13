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


def combine_analysis_command(p_dir_name, p, data):
    unitcon_path = os.path.join(unitcon_home, "unitcon")
    cmd = " ".join([unitcon_path, "analyze", p])
    target_analysis = data.get("analyze_target", None)
    skip_procedures = data.get("skip_procedures", None)
    if target_analysis != None:
        cmd = " ".join([cmd, "--target", data["target_source"] + ":" + str(data["target_line"])])
    if skip_procedures != None:
        cmd = " ".join([cmd, "--skip-procedures", data["skip_procedures"]])
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


def run(p_dir_name, p, time_out, proj_infos, log_file):
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


def copy(report_path, path):
    t_path = os.path.join(path, "unitcon-out", "unitcon-tests")
    log_path = os.path.join(path, "unitcon-out", "log.txt")
    shutil.copytree(t_path, os.path.join(report_path, p))
    shutil.copyfile(log_path, os.path.join(report_path, p, "log.txt"))


def all_run(p, path, data, report_path):
    print(f"build started: {p}")
    build(path)
    print(f"build done: {p}")

    print(f"analysis started: {p}")
    analyze(p, path, data)
    print(f"analysis done: {p}")
    
    if not os.path.isdir(report_path):
        os.mkdirs(report_path)
    unitcon_output = os.path.join(report_path, "unitcon-log.txt")

    print(f"synthesis started: {p}")
    run(p, path, args.time_out, data, unitcon_output)
    print(f"synthesis done: {p}")

    copy(report_path, path)



def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("target", type=str, help='[ target project name | all ]')
    parser.add_argument("--analysis_info", type=argparse.FileType('r'), default="script/bug.json")
    parser.add_argument("--time_out", type=int, default=300, help='time out (s)')
    args = parser.parse_args()

    bugs = json.load(args.analysis_info)
    target = bugs.get(args.target, None)
    datas = list()

    if args.target == "all":
        datas = bugs.items()
    elif target != None:
        datas = [(args.target, target)]


    for project, targets in datas:
        if "target_source" in targets:
            report_path = os.path.join(os.getenv("UNITCON_HOME"), "results", project)
            path = os.path.join(bench_home, project)
            all_run(project, path, targets, report_path)
        else:
            for subtarget, info in targets.items():
                report_path = os.path.join(os.getenv("UNITCON_HOME"), "results", project + "_" + subtarget)
                path = os.path.join(bench_home, project)
                all_run(project, path, info, report_path)


if __name__ == "__main__":
    main()
