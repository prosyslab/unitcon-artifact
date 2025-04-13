import venn
import csv
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.patches as patches

# order: UnitCon, EvoSuite, EvoFuzz, NPETest, UTBot


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

all_set = dict()
if not utbot:
    all_set["UTBot"] = set(utbot)
if not unitcon:
    all_set["UnitCon"] = set(unitcon)
if not evosuite:
    all_set["EvoSuite"] = set(evosuite)
if not evofuzz:
    all_set["EvoFuzz"] = set(evofuzz)
if not npetest:
    all_set["NPETest"] = set(npetest)

fig, ax = plt.subplots(figsize=(6, 6))

ax = venn.venn(all_set, figsize=fig, ax=ax)
legend = ax.get_legend()

plt.rc('text', usetex=True)
plt.legend(loc='upper center',
           bbox_to_anchor=(0.5, 1.15),
           ncol=3,
           fontsize=16)

# plt.margins(0.03)
plt.tight_layout()
plt.savefig('figure_6_b.pdf', format="pdf")
