""" RC3 unitcon with/without prioritization """
import csv
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt


def dur_or_nan(data, analysis_results):
    if data["validated"] == "False":
        return np.nan
    if not data["duration"]:
        return np.nan
    analysis_dur = float(analysis_results[data["project"]])
    return (float(data["duration"]) + analysis_dur)


def cumsum(lst):
    a = np.array(lst)
    a.sort()
    return a.cumsum()


def dur_sum(lst):
    cum = 0.0
    for i in lst:
        if np.isnan(i):
            continue
        cum += i
    return cum


with open("unitcon-strategies.csv", "r") as f:
    reader = csv.DictReader(f)
    rows = list(reader)

with open("infer.csv", "r") as f:
    reader = csv.DictReader(f)
    analysis_results = dict([(r["project"], r["duration"]) for r in reader])

basic = [dur_or_nan(r, analysis_results) for r in rows if r["type"] == "basic"]
prune = [dur_or_nan(r, analysis_results) for r in rows if r["type"] == "prune"]
prioritized = [dur_or_nan(r, analysis_results) for r in rows if r["type"] == "priority"]
unitcon = [dur_or_nan(r, analysis_results) for r in rows if r["type"] == "both"]

rows = list()
rows.append([0, 0, 0, 0]) # dummy
for b, pr, prior, u in zip(cumsum(basic), cumsum(prune), cumsum(prioritized), cumsum(unitcon)):
    rows.append([u, prior, pr, b])

df = pd.DataFrame.from_records(rows, columns=["Both", "Prioritization", "Pruning", "No Strategy"], index=range(0, len(rows)))
plt.rc('font', size=24)

ax = df.plot.line(xlabel="# Success (total=198)", ylabel="Time (seconds)", ms=8, style=["^-", "o-", "s-", "X-"],
                  figsize=(8, 5), markevery=[0, 5, 10, 15, 20, 25, 30, 35, 40, 44, 45, 48, 50, 54, 55, 60, 65, 70, 75, 80, 85, 90, 95, 97, 100, 104])
ax.tick_params(axis='both', labelsize=16)

succ_nums = [(44, dur_sum(basic)), (48, dur_sum(prune)), (97, dur_sum(prioritized)), (104, dur_sum(unitcon))]

for (num, accu) in succ_nums:
    plt.axvline(x=num, ymin=-2.5, ymax=accu/plt.ylim()[1], color='lightgrey', linestyle='--')

handles, labels = ax.get_legend_handles_labels()
plt.legend(handles=handles[::-1], labels=labels[::-1], fontsize=16)
plt.margins(0.05)
plt.tight_layout()
plt.savefig("figure_9_a.pdf", format="pdf")
