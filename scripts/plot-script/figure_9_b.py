import pandas as pd
import matplotlib.pyplot as plt


TO_value = 10 * 60
color_map = {
    'No Strategy': 'C3',
    'Pruning': 'C2',
    'Prioritization': 'C1',
    'Both': 'C0',
}

strategy_names = {
    'basic': 'No Strategy',
    'prune': 'Pruning',
    'priority': 'Prioritization',
    'both': 'Both'
}
bar_width = 0.5
bar_space = 0.2
grp_space = 0.5


df = pd.read_csv("unitcon-strategies.csv", index_col="project")
df_list = []
for typ in ['basic', 'prune', 'priority', 'both']:
  df_typ = df[df['type'] == typ]['duration'].rename(typ)
  df_list.append(df_typ)

duration_df = pd.concat(df_list, axis=1)

common = duration_df.dropna(how='all')

common['diff1'] = common["priority"] - common["both"]
common['diff2'] = common["basic"] - common["prune"]
for index in common.index:
  common.loc[index, 'diff'] = max(common.loc[index, 'diff1'], common.loc[index, 'diff2'])
targets = common.sort_values(by='diff', ascending=False)[:10][["basic", "prune", "priority", "both"]]
targets = targets.fillna(10 * 60 + 1).sort_values(by='basic', ascending=False).rename(strategy_names, axis=1)

plt.rc('font', size=24)
fig, ax = plt.subplots(1, figsize=(8, 5))
cur_xpos = 0.1
xtick_pos = dict()
for i in range(len(targets)):
    start_pos = cur_xpos
    for strategy, color in color_map.items():
        v = targets.iloc[i][strategy]
        plt.bar(cur_xpos, v, bar_width, color=color, alpha=0.7)
        cur_xpos += bar_width + bar_space
    end_pos = cur_xpos
    xtick_pos[f"P{i}"] = (start_pos + end_pos - bar_width - bar_space) / 2
    cur_xpos += grp_space

plt.xticks(list(xtick_pos.values()), list(xtick_pos.keys()), fontsize=16, rotation=0)
plt.tick_params(axis='x', bottom=False)

plt.yticks([0, 100, 200, 300, 400, 500, 600],
           [0, 100, 200, 300, 400, 500, 'T.O.'], fontsize=16, rotation=0)
plt.ylabel("Time (seconds)", fontdict={"size":24}, labelpad=2.0, loc='center')
plt.xlabel("Projects", fontdict={"size": 24}, labelpad=2.0, loc='center')

plt.legend(color_map.keys(), fontsize=16,
           loc='lower center', ncols=4, columnspacing=1.0,
           bbox_to_anchor=(0.5, 1.0))

plt.margins(0.01)
plt.tight_layout()
plt.savefig("figure_9_b.pdf", format="pdf")
