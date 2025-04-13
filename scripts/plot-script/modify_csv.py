import argparse
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os
import pathlib

FILE_PATH = os.path.dirname(os.path.abspath(__file__))
TOOL_LIST = [
    "unitcon", "evofuzz", "evofuzz-method", "evosuite", "evosuite-method", "npetest",
    "npetest-method", "randoop", "randoop-method", "utbot"
]


def create_unitcon_csv(results):
    file = os.path.join(results, "unitcon-results", "unitcon-report.csv")
    df = pd.read_csv(file, index_col="project")
    df = df[["validated"]]
    df.to_csv(os.path.join(FILE_PATH, "unitcon.csv"), index=True)


def create_unitcon_strategies(results):
    df_list = []
    for strategy in ["both", "priority", "prune", "basic"]:
        file = os.path.join(results, "unitcon-" + strategy + "-results", "unitcon-report.csv")
        df = pd.read_csv(file, index_col="project")
        df = df[["validated", "succ_time"]]
        df["succ_time"] = round(df["succ_time"], 2)
        df["succ_time"] = df["succ_time"].apply(lambda x: x if x else np.nan)
        df.rename(columns={"succ_time": "duration"}, inplace=True)
        df["type"] = strategy
        df_list.append(df)
    df = pd.concat(df_list)
    df.to_csv(os.path.join(FILE_PATH, "unitcon-strategies.csv"), index=True)


def create_csv(tool, iteration, results):
    df_list = []
    for trial in range(1, iteration + 1):
        file = os.path.join(results, str(trial), tool, tool + ".csv")
        df = pd.read_csv(file, index_col="project")
        df = df[["validated"]]
        df.rename(columns={"validated": str(trial)}, inplace=True)
        df_list.append(df)
    df = pd.concat(df_list, axis=1)
    print(os.path.join(FILE_PATH, tool + ".csv"))
    df.index.name = 'project'
    df.to_csv(os.path.join(FILE_PATH, tool + ".csv"), index=True)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("rq", type=str, help="number of RQ")
    parser.add_argument("--iteration", type=int, default=10, help="number of trials")
    parser.add_argument("--results",
                        type=pathlib.Path,
                        default=os.path.join(os.getenv("UNITCON_HOME"), "results"))
    args = parser.parse_args()

    if args.rq == "rq1":
        for tool in TOOL_LIST:
            if tool == "unitcon":
                create_unitcon_csv(args.results)
            else:
                create_csv(tool, args.iteration, args.results)
    elif args.rq == "rq3":
        create_unitcon_strategies(args.results)


if __name__ == "__main__":
    main()
