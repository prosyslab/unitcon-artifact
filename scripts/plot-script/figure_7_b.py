import argparse
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

fontsize_tick = 18
fontsize_label = 20
fontsize_tool = 16
fontsize_title = 22

plt.rc('text', usetex=True)


def get_min_max(tool, npe, data, iteration):
    if tool == "UnitCon":
        data = npe.merge(data, on="project", how="left").sum()
        value = data.loc["validated"]
        return (value, value, value)
    min_value = 500
    max_value = 0
    accu = 0
    data = npe.merge(data, on="project", how="left").sum()
    for seed in range(1, iteration+1):
        curr_value = data.loc[str(seed)]
        if min_value > curr_value:
            min_value = curr_value
        if max_value < curr_value:
            max_value = curr_value
        accu += curr_value
    return (min_value, max_value, accu / iteration)

def draw(iteration):
    tool_list = ["UnitCon", "EvoSuite", "EvoFuzz", "NPETest", "UTBot", "Randoop"]

    unitcon = pd.read_csv("unitcon.csv", index_col="project")
    evosuite = pd.read_csv("evosuite.csv", index_col="project")
    evofuzz = pd.read_csv("evofuzz.csv", index_col="project")
    npetest = pd.read_csv("npetest.csv", index_col="project")
    randoop = pd.read_csv("randoop.csv", index_col="project")
    utbot = pd.read_csv("utbot.csv", index_col="project")

    npe_info = pd.read_csv("npe.csv", index_col="project")
    npe = npe_info[npe_info["npe"]=="x"]

    tool_set = {
        "UnitCon": unitcon,
        "EvoSuite": evosuite,
        "EvoFuzz": evofuzz,
        "NPETest": npetest,
        "UTBot": utbot,
        "Randoop": randoop
    }

    color_map = {
        "UnitCon": "C0",
        "NPETest": "C3",
        "EvoSuite": "C1",
        "UTBot": "C4",
        "EvoFuzz": "C2",
        "Randoop": "C5"
    }

    x = np.arange(len(tool_list))
    fig, ax = plt.subplots(figsize=(8, 5))

    bar_width = 0.6
    cur_xpos = 0.1
    bar_space = 0.1
    grp_space = 0.8

    xtick_pos = dict()
    xtick = list()


    for tool in range(0, len(tool_list)):
        start_pos = cur_xpos
        tool = tool_list[tool]
        min_value, max_value, std = get_min_max(tool, npe, tool_set[tool], iteration)
        bar = ax.bar(cur_xpos, std, width=bar_width, color=color_map[tool])
        xtick.append(bar)

        if min_value != max_value:
            ax.errorbar(cur_xpos,
                        std,
                        yerr=np.vstack([std - min_value, max_value - std]),
                        fmt='none',
                        ecolor='black',
                        capsize=5,
                        capthick=1)
        cur_xpos += bar_width + bar_space
        end_pos = cur_xpos
        xtick_pos[tool] = (start_pos + end_pos - bar_width) / 2
        cur_xpos += grp_space

    tool_label = [r'\textsc{' + tool + '}' for tool in tool_list]

    ax.set_xticks(list(xtick_pos.values()),
                labels=tool_label,
                fontsize=fontsize_tick,
                rotation=0)
    ax.tick_params(axis='x', bottom=False, labelsize=fontsize_tick)

    plt.yticks([0, 15, 30, 45, 60, 66], [0, 15, 30, 45, 60, 66],
            fontsize=fontsize_tick,
            rotation=0)

    _, u_max, _ = get_min_max("UnitCon", npe, tool_set["UnitCon"], iteration)

    plt.axhline(u_max,
                color=color_map["UnitCon"],
                linestyle='--',
                linewidth=2,
                label='_nolegend_',
                zorder=0)

    # plot max
    plt.axhline(y=70, color='grey', alpha=0, linestyle='-', linewidth=2, label='_nolegend_')

    plt.margins(0.03)
    plt.tight_layout()
    plt.savefig("figure_7_b.pdf", format="pdf")

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--iteration", type=int, default=10, help="number of trials")
    args = parser.parse_args()

    draw(args.iteration)


if __name__ == "__main__":
    main()
