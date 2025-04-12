import venn
import csv
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.patches as patches

# order: UnitCon, EvoSuite, EvoFuzz, NPETest, UTBot


def get_new_handles(handles):
    return [handles[1], handles[4], handles[2], handles[0], handles[3]]


def get_new_labels(labels):
    unitcon = r'\textsc{UnitCon}'
    evosuite = r'\textsc{EvoSuite}'
    evofuzz = r'\textsc{EvoFuzz}'
    npetest = r'\textsc{NPETest}'
    utbot = r'\textsc{UTBot}'
    return [unitcon, npetest, evosuite, utbot, evofuzz]

unitcon = pd.read_csv("unitcon.csv", index_col="project")
evosuite = pd.read_csv("evosuite.csv", index_col="project").apply(lambda row: (row.eq(True)).any(), axis=1)
evofuzz = pd.read_csv("evofuzz.csv", index_col="project").apply(lambda row: (row.eq(True)).any(), axis=1)
npetest = pd.read_csv("npetest.csv", index_col="project").apply(lambda row: (row.eq(True)).any(), axis=1)
utbot = pd.read_csv("utbot.csv", index_col="project").apply(lambda row: (row.eq(True)).any(), axis=1)

evosuite = pd.DataFrame({
    "project": evosuite.index,
    "validated": evosuite
})
evofuzz = pd.DataFrame({
    "project": evofuzz.index,
    "validated": evofuzz
})
npetest = pd.DataFrame({
    "project": npetest.index,
    "validated": npetest
})
utbot = pd.DataFrame({
    "project": utbot.index,
    "validated": utbot
})


unitcon = [index for index, r in unitcon.iterrows() if r["validated"]]
evosuite = [r["project"] for r in evosuite.to_dict(orient="records") if r["validated"]]
evofuzz = [r["project"] for r in evofuzz.to_dict(orient="records") if r["validated"]]
npetest = [r["project"] for r in npetest.to_dict(orient="records") if r["validated"]]
utbot = [r["project"] for r in utbot.to_dict(orient="records") if r["validated"]]

all_set = {
    "UTBot": set(utbot),
    "UnitCon": set(unitcon),
    "EvoSuite": set(evosuite),
    "EvoFuzz": set(evofuzz),
    "NPETest": set(npetest),
}

fig, ax = plt.subplots(figsize=(6, 6))

color_map = {
    "UnitCon": "#6BAED6",
    "EvoSuite": "#FD8D3C",
    "EvoFuzz": "#74C476",
    "NPETest": "#FF9896",
    "UTBot": "#9E9AC8"
}

cmap = ["#9E9AC8", "#6BAED6", "#FD8D3C", "#74C476", "#FF9896"]

ax = venn.venn(all_set, figsize=fig, ax=ax, cmap=cmap)
legend = ax.get_legend()
handles, labels = legend.legend_handles, [t.get_text() for t in legend.texts]

plt.rc('text', usetex=True)
plt.legend(handles=get_new_handles(handles),
           labels=get_new_labels(labels),
           loc='upper center',
           bbox_to_anchor=(0.5, 1.15),
           ncol=3,
           fontsize=16)

# plt.margins(0.03)
plt.tight_layout()
plt.savefig('figure_6_b.pdf', format="pdf")
