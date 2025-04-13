import argparse
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

fontsize_tick = 18
fontsize_label = 20
fontsize_tool = 16
fontsize_legend = 12
fontsize_title = 22

plt.rc('text', usetex=True)


def get_min_max(tool, data, iteration):
    if tool == "UnitCon":
        validated = data.loc["validated"]
        return (validated, validated, validated)
    min_value = 500
    max_value = 0
    accu = 0
    for seed in range(1, iteration+1):
        curr_value = data.loc[str(seed)]
        if min_value > curr_value:
            min_value = curr_value
        if max_value < curr_value:
            max_value = curr_value
        accu += curr_value
    return (min_value, max_value, accu / 10)

def draw(iteration):
    unitcon = pd.read_csv("unitcon.csv", index_col="project").sum()
    evosuite = pd.read_csv("evosuite.csv", index_col="project").sum()
    evosuite_method = pd.read_csv("evosuite-method.csv", index_col="project").sum()
    evofuzz = pd.read_csv("evofuzz.csv", index_col="project").sum()
    evofuzz_method = pd.read_csv("evofuzz-method.csv", index_col="project").sum()
    npetest = pd.read_csv("npetest.csv", index_col="project").sum()
    npetest_method = pd.read_csv("npetest-method.csv", index_col="project").sum()
    randoop = pd.read_csv("randoop.csv", index_col="project").sum()
    randoop_method = pd.read_csv("randoop-method.csv", index_col="project").sum()
    utbot = pd.read_csv("utbot.csv", index_col="project").sum()

    tool_list = [
        "UnitCon", "EvoSuite", "EvoFuzz", "NPETest", "UTBot", "Randoop"
    ]

    tool_set = {
        "UnitCon": unitcon,
        "EvoSuite": evosuite,
        "EvoSuite_method": evosuite_method,
        "EvoFuzz": evofuzz,
        "EvoFuzz_method": evofuzz_method,
        "NPETest": npetest,
        "NPETest_method": npetest_method,
        "UTBot": utbot,
        "Randoop": randoop,
        "Randoop_method": randoop_method
    }

    color_map = {
        "UnitCon": "C0",
        "EvoSuite": "C1",
        "EvoSuite_method": "#FFBB78",
        "EvoFuzz": "C2",
        "EvoFuzz_method": "#98DF8A",
        "NPETest": "C3",
        "NPETest_method": "#FF9896",
        "UTBot": "C4",
        "Randoop": "C5",
        "Randoop_method": "#C49C94"
    }

    x = np.arange(len(tool_list))
    fig, ax = plt.subplots(figsize=(8, 5.5))

    bar_width = 0.6
    cur_xpos = 0.1
    bar_space = 0.2
    grp_space = 0.5

    xtick_pos = dict()

    for idx in range(0, len(tool_list)):
        start_pos = cur_xpos
        tool = tool_list[idx]
        min_value, max_value, std = get_min_max(tool, tool_set[tool], iteration)

        ax.bar(cur_xpos, std, width=bar_width, color=color_map[tool])
        if min_value != max_value:
            ax.errorbar(cur_xpos,
                        std,
                        yerr=np.vstack([std - min_value, max_value - std]),
                        fmt='none',
                        ecolor='black',
                        capsize=5,
                        capthick=1)
        cur_xpos += bar_width + bar_space

        if tool in ["EvoSuite", "EvoFuzz", "NPETest", "Randoop"]:
            min_value2, max_value2, std2 = get_min_max(tool+"_method", tool_set[tool+"_method"], iteration)
            ax.bar(cur_xpos, std2, bar_width, color=color_map[tool+"_method"])
            ax.errorbar(cur_xpos,
                    std2,
                    yerr=np.vstack([std2 - min_value2, max_value2 - std2]),
                    fmt='none',
                    ecolor='black',
                    capsize=5,
                    capthick=1)
            cur_xpos += bar_width + bar_space
        end_pos = cur_xpos
        xtick_pos[tool] = (start_pos + end_pos - bar_width - bar_space) / 2
        cur_xpos += grp_space


    labels = [r'\textsc{'+tool+'}' for tool in xtick_pos.keys()] 

    plt.xticks(list(xtick_pos.values()), labels, fontsize=fontsize_tick, rotation=0)
    ax.tick_params(axis='x', bottom=False, labelsize=fontsize_tool, pad=18)

    for tool in ["EvoSuite", "EvoFuzz", "NPETest", "Randoop"]:
        ax.text(xtick_pos[tool] - (bar_width + bar_space) /2, -5, r'\texttt{class}', ha="center", fontsize=fontsize_legend)
        ax.text(xtick_pos[tool] + (bar_width + bar_space) /2, -5, r'\texttt{method}', ha="center", fontsize=fontsize_legend)

    ax.text(xtick_pos["UTBot"], -5, r'\texttt{class}', ha="center", fontsize=fontsize_legend)

    plt.yticks([0, 30, 60, 90, 104], [0, 30, 60, 90, 104], fontsize=fontsize_tick, rotation=0)

    # plot max
    plt.axhline(y=115, color=color_map["UnitCon"], alpha=0, linestyle='-', linewidth=2)
    plt.axhline(y=104, color=color_map["UnitCon"], linestyle='--')

    plt.margins(0.03)
    plt.tight_layout()
    plt.savefig("figure_6_a.pdf", format="pdf")

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--iteration", type=int, default=10, help="number of trials")
    args = parser.parse_args()

    draw(args.iteration)


if __name__ == "__main__":
    main()