#!/bin/bash

# tablesaw-65596d8 project
cd $BENCH_HOME/Maven/tablesaw_65596d8
git init

# zookeeper-e41cac8 project
cd $BENCH_HOME/Maven/zookeeper_e41cac8
git init
git config --global --add safe.directory /opt/benchmarks/Maven/zookeeper_e41cac8
git config --global user.email "john.doe@gmail.com"
git add .
git commit -m "."

source ~/.bashrc
cd /root/
