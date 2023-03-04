import sys, os, time, subprocess, random
import threading
from multiprocessing import Process
import matplotlib.pyplot as plt
import palettable
import numpy as np
import pandas as pd
import csv

colors = palettable.colorbrewer.qualitative.Paired_10.hex_colors
linestyles = ['-', '--', ":"]
# plt.rc('font',**{'size': 16, 'family': 'Arial' })
plt.rc('pdf',fonttype = 42)

def allflow_1():
    fig, ax = plt.subplots(figsize=(7, 4))
    with open('./plot.csv') as file:
        reader = csv.reader(file)
        count=0
        for row in reader:
            if count==0:
                fct_dctcp_mean=[]
                fct_dctcp_median=[]
                fct_dctcp_99=[]
                for i in range(16,23):
                    fct_dctcp_mean.append(float(row[i]))
                for i in range(23,30):
                    fct_dctcp_median.append(float(row[i]))
                for i in range(30,37):
                    fct_dctcp_99.append(float(row[i]))
            elif count==1:
                fct_pifo_mean=[]
                fct_pifo_median=[]
                fct_pifo_99=[]
                for i in range(16,23):
                    fct_pifo_mean.append(float(row[i]))
                for i in range(23,30):
                    fct_pifo_median.append(float(row[i]))
                for i in range(30,37):
                    fct_pifo_99.append(float(row[i]))
            elif count==2:
                fct_aifo_mean=[]
                fct_aifo_median=[]
                fct_aifo_99=[]
                for i in range(16,23):
                    fct_aifo_mean.append(float(row[i]))
                for i in range(23,30):
                    fct_aifo_median.append(float(row[i]))
                for i in range(30,37):
                    fct_aifo_99.append(float(row[i]))
            elif count==3:
                fct_sqwfq_mean=[]
                fct_sqwfq_median=[]
                fct_sqwfq_99=[]
                for i in range(16,23):
                    fct_sqwfq_mean.append(float(row[i]))
                for i in range(23,30):
                    fct_sqwfq_median.append(float(row[i]))
                for i in range(30,37):
                    fct_sqwfq_99.append(float(row[i]))
            elif count==4:
                fct_sqswfq_mean=[]
                fct_sqswfq_median=[]
                fct_sqswfq_99=[]
                for i in range(16,23):
                    fct_sqswfq_mean.append(float(row[i]))
                for i in range(23,30):
                    fct_sqswfq_median.append(float(row[i]))
                for i in range(30,37):
                    fct_sqswfq_99.append(float(row[i]))
            elif count==5:
                fct_pcq_mean=[]
                fct_pcq_median=[]
                fct_pcq_99=[]
                for i in range(16,23):
                    fct_pcq_mean.append(float(row[i]))
                for i in range(23,30):
                    fct_pcq_median.append(float(row[i]))
                for i in range(30,37):
                    fct_pcq_99.append(float(row[i]))
            count += 1

    _mean_funcs = [fct_dctcp_mean,fct_pifo_mean,fct_aifo_mean,fct_sqwfq_mean,fct_sqswfq_mean,fct_pcq_mean]
    _median_funcs = [fct_dctcp_median,fct_pifo_median,fct_aifo_median,fct_sqwfq_median,fct_sqswfq_median,fct_pcq_median]
    _99th_funcs = [fct_dctcp_99,fct_pifo_99,fct_aifo_99,fct_sqwfq_99,fct_sqswfq_99,fct_pcq_99]

    for x in range(len(_mean_funcs)):
        for i in range(len(_99th_funcs[x])):
            _99th_funcs[x][i] = _99th_funcs[x][i] - _mean_funcs[x][i]

    width = 0.1
    idx = [[0.1, 0.8, 1.5, 2.2, 2.9, 3.6, 4.3],
        [0.2, 0.9, 1.6, 2.3, 3.0, 3.7, 4.4],
        [0.3, 1.0, 1.7, 2.4, 3.1, 3.8, 4.5],
        [0.4, 1.1, 1.8, 2.5, 3.2, 3.9, 4.6],
        [0.5, 1.2, 1.9, 2.6, 3.3, 4.0, 4.7],
        [0.6, 1.3, 2.0, 2.7, 3.4, 4.1, 4.8]]

    xticks_idx = [0.35, 1.05, 1.75, 2.45, 3.15, 3.85, 4.55]
    # xticks_idx = idx[2]
    xticks = ["10K", "20K", "30K", "50K", "80K", "0.1M-1M", "$\geq$2M"]
    plt.xticks(xticks_idx, xticks)
    low = [0, 0, 0, 0, 0, 0, 0]
    ax.bar(idx[0], _mean_funcs[0], yerr=(low, _99th_funcs[0]), error_kw=dict(lw=1, capsize=3, capthick=1), width=width, align='center', ecolor=colors[6], capsize=10, label = "DCTCP", color=colors[6])
    ax.bar(idx[1], _mean_funcs[2], yerr=(low, _99th_funcs[2]), error_kw=dict(lw=1, capsize=3, capthick=1), width=width, align='center', ecolor=colors[9], capsize=10, label = "AIFO", color=colors[9])
    ax.bar(idx[2], _mean_funcs[5], yerr=(low, _99th_funcs[5]), error_kw=dict(lw=1, capsize=3, capthick=1), width=width, align='center', ecolor=colors[5], capsize=10, label = "PCQ", color=colors[5])
    ax.bar(idx[3], _mean_funcs[3], yerr=(low, _99th_funcs[3]), error_kw=dict(lw=1, capsize=3, capthick=1), width=width, align='center', ecolor=colors[4], capsize=10, label = "SQWFQ", color=colors[4])
    ax.bar(idx[4], _mean_funcs[4], yerr=(low, _99th_funcs[4]), error_kw=dict(lw=1, capsize=3, capthick=1), width=width, align='center', ecolor=colors[3], capsize=10, label = "SQSWFQ", color=colors[3])
    ax.bar(idx[5], _mean_funcs[1], yerr=(low, _99th_funcs[1]), error_kw=dict(lw=1, capsize=3, capthick=1), width=width, align='center', ecolor=colors[1], capsize=10, label = "PIFO", color=colors[1])

    l = plt.legend(numpoints=1, prop={'size':14}, loc='upper center', bbox_to_anchor=(0.5, 1.25), ncol=3)
    l.set_frame_on(False)

    ax.set_yscale('log')
    ax.set_xlabel('Flow size')
    ax.xaxis.set_ticks_position('bottom')
    ax.set_ylabel('Flow completion time (ms)')
    ax.yaxis.set_ticks_position('left')

    plt.savefig("allflow.pdf", bbox_inches='tight')
    return

def allflow_load(load,_mean_funcs,_99th_funcs):
    fig, ax = plt.subplots(figsize=(7, 4))

    for x in range(len(_mean_funcs)):
        for i in range(len(_99th_funcs[x])):
            _99th_funcs[x][i] = _99th_funcs[x][i] - _mean_funcs[x][i]

    width = 0.1
    idx = [[0.1, 0.8, 1.5, 2.2, 2.9, 3.6, 4.3],
        [0.2, 0.9, 1.6, 2.3, 3.0, 3.7, 4.4],
        [0.3, 1.0, 1.7, 2.4, 3.1, 3.8, 4.5],
        [0.4, 1.1, 1.8, 2.5, 3.2, 3.9, 4.6],
        [0.5, 1.2, 1.9, 2.6, 3.3, 4.0, 4.7],
        [0.6, 1.3, 2.0, 2.7, 3.4, 4.1, 4.8]]

    xticks_idx = [0.35, 1.05, 1.75, 2.45, 3.15, 3.85, 4.55]
    # xticks_idx = idx[2]
    xticks = ["10K", "20K", "30K", "50K", "80K", "0.1M-1M", "$\geq$2M"]
    plt.xticks(xticks_idx, xticks)
    low = [0, 0, 0, 0, 0, 0, 0]
    ax.bar(idx[0], _mean_funcs[0], yerr=(low, _99th_funcs[0]), error_kw=dict(lw=1, capsize=3, capthick=1), width=width, align='center', ecolor=colors[6], capsize=10, label = "DCTCP", color=colors[6])
    ax.bar(idx[1], _mean_funcs[2], yerr=(low, _99th_funcs[2]), error_kw=dict(lw=1, capsize=3, capthick=1), width=width, align='center', ecolor=colors[9], capsize=10, label = "AIFO", color=colors[9])
    ax.bar(idx[2], _mean_funcs[5], yerr=(low, _99th_funcs[5]), error_kw=dict(lw=1, capsize=3, capthick=1), width=width, align='center', ecolor=colors[5], capsize=10, label = "PCQ", color=colors[5])
    ax.bar(idx[3], _mean_funcs[3], yerr=(low, _99th_funcs[3]), error_kw=dict(lw=1, capsize=3, capthick=1), width=width, align='center', ecolor=colors[4], capsize=10, label = "SQWFQ", color=colors[4])
    ax.bar(idx[4], _mean_funcs[4], yerr=(low, _99th_funcs[4]), error_kw=dict(lw=1, capsize=3, capthick=1), width=width, align='center', ecolor=colors[3], capsize=10, label = "SQSWFQ", color=colors[3])
    ax.bar(idx[5], _mean_funcs[1], yerr=(low, _99th_funcs[1]), error_kw=dict(lw=1, capsize=3, capthick=1), width=width, align='center', ecolor=colors[1], capsize=10, label = "PIFO", color=colors[1])

    l = plt.legend(numpoints=1, prop={'size':14}, loc='upper center', bbox_to_anchor=(0.5, 1.25), ncol=3)
    l.set_frame_on(False)

    ax.set_yscale('log')
    ax.set_xlabel('Flow size')
    ax.xaxis.set_ticks_position('bottom')
    ax.set_ylabel('Flow completion time (ms)')
    ax.yaxis.set_ticks_position('left')

    plt.savefig("allflow"+str(load)+".pdf", bbox_inches='tight')
    return

def allflow_eachload():
    with open('./plot.csv') as file:
        reader = csv.reader(file)
        count=0
        load=0
        for row in reader:
            if count==0:
                fct_dctcp_mean=[]
                fct_dctcp_median=[]
                fct_dctcp_99=[]
                for i in range(16,23):
                    fct_dctcp_mean.append(float(row[i]))
                for i in range(23,30):
                    fct_dctcp_median.append(float(row[i]))
                for i in range(30,37):
                    fct_dctcp_99.append(float(row[i]))
            elif count==1:
                fct_pifo_mean=[]
                fct_pifo_median=[]
                fct_pifo_99=[]
                for i in range(16,23):
                    fct_pifo_mean.append(float(row[i]))
                for i in range(23,30):
                    fct_pifo_median.append(float(row[i]))
                for i in range(30,37):
                    fct_pifo_99.append(float(row[i]))
            elif count==2:
                fct_aifo_mean=[]
                fct_aifo_median=[]
                fct_aifo_99=[]
                for i in range(16,23):
                    fct_aifo_mean.append(float(row[i]))
                for i in range(23,30):
                    fct_aifo_median.append(float(row[i]))
                for i in range(30,37):
                    fct_aifo_99.append(float(row[i]))
            elif count==3:
                fct_sqwfq_mean=[]
                fct_sqwfq_median=[]
                fct_sqwfq_99=[]
                for i in range(16,23):
                    fct_sqwfq_mean.append(float(row[i]))
                for i in range(23,30):
                    fct_sqwfq_median.append(float(row[i]))
                for i in range(30,37):
                    fct_sqwfq_99.append(float(row[i]))
            elif count==4:
                fct_sqswfq_mean=[]
                fct_sqswfq_median=[]
                fct_sqswfq_99=[]
                for i in range(16,23):
                    fct_sqswfq_mean.append(float(row[i]))
                for i in range(23,30):
                    fct_sqswfq_median.append(float(row[i]))
                for i in range(30,37):
                    fct_sqswfq_99.append(float(row[i]))
            elif count==5:
                fct_pcq_mean=[]
                fct_pcq_median=[]
                fct_pcq_99=[]
                for i in range(16,23):
                    fct_pcq_mean.append(float(row[i]))
                for i in range(23,30):
                    fct_pcq_median.append(float(row[i]))
                for i in range(30,37):
                    fct_pcq_99.append(float(row[i]))
            count += 1
            if count == 6:
                count = 0
                _mean_funcs = [fct_dctcp_mean,fct_pifo_mean,fct_aifo_mean,fct_sqwfq_mean,fct_sqswfq_mean,fct_pcq_mean]
                _median_funcs = [fct_dctcp_median,fct_pifo_median,fct_aifo_median,fct_sqwfq_median,fct_sqswfq_median,fct_pcq_median]
                _99th_funcs = [fct_dctcp_99,fct_pifo_99,fct_aifo_99,fct_sqwfq_99,fct_sqswfq_99,fct_pcq_99]
                allflow_load(load,_mean_funcs,_99th_funcs)
                load += 1

def smallflow10k():
    with open('./plot.csv') as file:
        reader = csv.reader(file)
        count = 0
        dctcp_mean = []
        pifo_mean = []
        aifo_mean = []
        sqwfq_mean = []
        sqswfq_mean = []
        pcq_mean = []
        dctcp_99 = []
        pifo_99 = []
        aifo_99 = []
        sqwfq_99 = []
        sqswfq_99 = []
        pcq_99 = []
        for row in reader:
            if count==0:
                dctcp_mean.append(float(row[6]))
                dctcp_99.append(float(row[8]))
            if count==1:
                pifo_mean.append(float(row[6]))
                pifo_99.append(float(row[8]))
            if count==2:
                aifo_mean.append(float(row[6]))
                aifo_99.append(float(row[8]))
            if count==3:
                sqwfq_mean.append(float(row[6]))
                sqwfq_99.append(float(row[8]))
            if count==4:
                sqswfq_mean.append(float(row[6]))
                sqswfq_99.append(float(row[8]))
            if count==5:
                pcq_mean.append(float(row[6]))
                pcq_99.append(float(row[8]))
            count += 1
            if count == 6:
                count=0

    fig, ax = plt.subplots(figsize=(5, 3))

    # idx = [0.1, 0.3, 0.5, 0.7, 0.9]
    # idx = [7200, 10800, 14400, 18000, 21600]
    idx = [0,1,2,3,4,5,6,7]

    plt.plot(idx, dctcp_mean, label='DCTCP', color=colors[6],\
            marker='*', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, pifo_mean, label='PIFO', color=colors[1],\
            marker='D', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, aifo_mean, label='AIFO', color=colors[9],\
            marker='P', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, sqwfq_mean, label='SQWFQ', color=colors[4],\
            marker='^', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, sqswfq_mean, label='SQSWFQ', color=colors[3],\
            marker='2', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, pcq_mean, label='PCQ', color=colors[5],\
            marker='s', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)

    # ax.set_xlim(0.1, 0.9)
    # ax.set_xticks(([0.1, 0.3, 0.5, 0.7, 0.9]))
    # ax.set_xlabel('Load')
    # ax.set_xlim(7200, 21600)
    # ax.set_xticks(([7200, 10800, 14400, 18000, 21600]))
    ax.set_xlim(0,7)
    ax.set_xticks(([0,1,2,3,4,5,6,7]))
    ax.set_xticklabels([7000,8500,10000,11500,13000,14500,16000,17500])
    ax.set_xlabel('Load_traffic_lamda')
    ax.xaxis.set_ticks_position('bottom')
    ax.set_ylabel('Flow completion time (ms)')
    ax.set_ylim(ymin=0, ymax=100)
    # ax.set_yticks(([0, 30, 60, 90, 120]))
    ax.yaxis.set_ticks_position('left')

    l = plt.legend(loc='upper left', numpoints=1, prop={'size':12}, labelspacing=0.36)
    l.set_frame_on(False)
    plt.savefig("smallflow10k_mean.pdf", bbox_inches='tight')

    fig1, ax1 = plt.subplots(figsize=(5, 3))

    # idx = [0.1, 0.3, 0.5, 0.7, 0.9]
    # idx = [7200, 10800, 14400, 18000, 21600]
    idx = [0,1,2,3,4,5,6,7]

    plt.plot(idx, dctcp_99, label='DCTCP', color=colors[6],\
            marker='*', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, pifo_99, label='PIFO', color=colors[1],\
            marker='D', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, aifo_99, label='AIFO', color=colors[9],\
            marker='P', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, sqwfq_99, label='SQWFQ', color=colors[4],\
            marker='^', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, sqswfq_99, label='SQSWFQ', color=colors[3],\
            marker='2', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, pcq_99, label='PCQ', color=colors[5],\
            marker='s', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)

    # ax1.set_xlim(0.1, 0.9)
    # ax1.set_xticks(([0.1, 0.3, 0.5, 0.7, 0.9]))
    # ax1.set_xlabel('Load')
    # ax1.set_xlim(7200, 21600)
    # ax1.set_xticks(([7200, 10800, 14400, 18000, 21600]))
    ax1.set_xlim(0,7)
    ax1.set_xticks(([0,1,2,3,4,5,6,7]))
    ax1.set_xticklabels([7000,8500,10000,11500,13000,14500,16000,17500])
    ax1.set_xlabel('Load_traffic_lamda')
    ax1.xaxis.set_ticks_position('bottom')
    ax1.set_ylabel('Flow completion time (ms)')
    ax1.set_ylim(ymin=0, ymax=300)
    # ax1.set_yticks(([0, 30, 60, 90, 120]))
    ax1.yaxis.set_ticks_position('left')

    l = plt.legend(loc='upper left', numpoints=1, prop={'size':12}, labelspacing=0.36)
    l.set_frame_on(False)
    plt.savefig("smallflow10k_99.pdf", bbox_inches='tight')
    return

def smallflow100k():
    with open('./plot.csv') as file:
        reader = csv.reader(file)
        count = 0
        dctcp_mean = []
        pifo_mean = []
        aifo_mean = []
        sqwfq_mean = []
        sqswfq_mean = []
        pcq_mean = []
        dctcp_99 = []
        pifo_99 = []
        aifo_99 = []
        sqwfq_99 = []
        sqswfq_99 = []
        pcq_99 = []
        for row in reader:
            if count==0:
                dctcp_mean.append(float(row[11]))
                dctcp_99.append(float(row[13]))
            if count==1:
                pifo_mean.append(float(row[11]))
                pifo_99.append(float(row[13]))
            if count==2:
                aifo_mean.append(float(row[11]))
                aifo_99.append(float(row[13]))
            if count==3:
                sqwfq_mean.append(float(row[11]))
                sqwfq_99.append(float(row[13]))
            if count==4:
                sqswfq_mean.append(float(row[11]))
                sqswfq_99.append(float(row[13]))
            if count==5:
                pcq_mean.append(float(row[11]))
                pcq_99.append(float(row[13]))
            count += 1
            if count == 6:
                count=0

    fig, ax = plt.subplots(figsize=(5, 3))

    # idx = [0.1, 0.3, 0.5, 0.7, 0.9]
    # idx = [7200, 10800, 14400, 18000, 21600]
    idx = [0,1,2,3,4,5,6,7]

    plt.plot(idx, dctcp_mean, label='DCTCP', color=colors[6],\
            marker='*', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, pifo_mean, label='PIFO', color=colors[1],\
            marker='D', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, aifo_mean, label='AIFO', color=colors[9],\
            marker='P', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, sqwfq_mean, label='SQWFQ', color=colors[4],\
            marker='^', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, sqswfq_mean, label='SQSWFQ', color=colors[3],\
            marker='2', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, pcq_mean, label='PCQ', color=colors[5],\
            marker='s', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)

    # ax.set_xlim(0.1, 0.9)
    # ax.set_xticks(([0.1, 0.3, 0.5, 0.7, 0.9]))
    # ax.set_xlabel('Load')
    # ax.set_xlim(7200, 21600)
    # ax.set_xticks(([7200, 10800, 14400, 18000, 21600]))
    ax.set_xlim(0,7)
    ax.set_xticks(([0,1,2,3,4,5,6,7]))
    ax.set_xticklabels([7000,8500,10000,11500,13000,14500,16000,17500])
    ax.set_xlabel('Load_traffic_lamda')
    ax.xaxis.set_ticks_position('bottom')
    ax.set_ylabel('Flow completion time (ms)')
    ax.set_ylim(ymin=0, ymax=100)
    # ax.set_yticks(([0, 30, 60, 90, 120]))
    ax.yaxis.set_ticks_position('left')

    l = plt.legend(loc='upper left', numpoints=1, prop={'size':12}, labelspacing=0.36)
    l.set_frame_on(False)
    plt.savefig("smallflow100k_mean.pdf", bbox_inches='tight')

    fig1, ax1 = plt.subplots(figsize=(5, 3))

    # idx = [0.1, 0.3, 0.5, 0.7, 0.9]
    # idx = [7200, 10800, 14400, 18000, 21600]
    idx = [0,1,2,3,4,5,6,7]

    plt.plot(idx, dctcp_99, label='DCTCP', color=colors[6],\
            marker='*', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, pifo_99, label='PIFO', color=colors[1],\
            marker='D', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, aifo_99, label='AIFO', color=colors[9],\
            marker='P', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, sqwfq_99, label='SQWFQ', color=colors[4],\
            marker='^', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, sqswfq_99, label='SQSWFQ', color=colors[3],\
            marker='2', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)
    plt.plot(idx, pcq_99, label='PCQ', color=colors[5],\
            marker='s', markersize=6, lw=1, linestyle=linestyles[0], clip_on=False)

    # ax1.set_xlim(0.1, 0.9)
    # ax1.set_xticks(([0.1, 0.3, 0.5, 0.7, 0.9]))
    # ax1.set_xlabel('Load')
    # ax1.set_xlim(7200, 21600)
    # ax1.set_xticks(([7200, 10800, 14400, 18000, 21600]))
    ax1.set_xlim(0,7)
    ax1.set_xticks(([0,1,2,3,4,5,6,7]))
    ax1.set_xticklabels([7000,8500,10000,11500,13000,14500,16000,17500])
    ax1.set_xlabel('Load_traffic_lamda')
    ax1.xaxis.set_ticks_position('bottom')
    ax1.set_ylabel('Flow completion time (ms)')
    ax1.set_ylim(ymin=0, ymax=300)
    # ax1.set_yticks(([0, 30, 60, 90, 120]))
    ax1.yaxis.set_ticks_position('left')

    l = plt.legend(loc='upper left', numpoints=1, prop={'size':12}, labelspacing=0.36)
    l.set_frame_on(False)
    plt.savefig("smallflow100k_99.pdf", bbox_inches='tight')
    return

def main():
    # allflow_1()
    allflow_eachload()
    smallflow10k()
    smallflow100k()

if __name__ == '__main__':
    main()