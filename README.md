# UnitCon: Synthesizing Targeted Unit Tests for Java Runtime Exceptions (Paper Artifact)

This is the artifact of the paper *UnitCon: Synthesizing Targeted Unit Tests for Java Runtime Exceptions* to appear in FSE 2025.

## __1. Getting started__
### __1.1. System requirements__
To run the experiments in the paper, we used Linux machines with 512GB RAM and Intel Xeon 2.90GHz.
The experiment requires **188** GB storage for Docker images and experimental results.

Additionally, we assume that the following environment settings are met.
- Ubuntu 22.04
- python 3.8+
- pip3
- Docker

### __1.2. Preparing the Docker image__
Our artifact is composed of two parts: the Docker image and the framework to build and utilize it. The Docker image contains all the necessary tools and dependencies to run the experiments.
The framework, that holds this `README` file, is used to build the Docker image and orchestrate the experiments.

#### Option 1. Pull the pre-built Docker image (Recommended)
You can pull the pre-built Docker image from Dockerhub. 
To do so, run
```
$ docker pull prosyslab/unitcon-artifact
```
Note that one of the tools used in the experiments requires a different Docker image. You can pull this Docker image.
```
$ docker pull prosyslab/unitcon-artifact-utbot
```

#### Option 2. Build the Docker image manually
If you want to build the Docker image manually, you need to download the dependencies.

Please download `m2-cache.tar` from the **XXX** path, place it inside the `resources` directory, and then run the following command to build the image.
```
$ docker build -t prosyslab/unitcon-artifact -f Dockerfile .
$ docker build -t prosyslab/unitcon-artifact-utbot -f Dockerfile.utbot .
```

Both commands take about **10** minutes to build each docker image, respectively.

### __1.3. Start Docker containers and check their validity__
1. Start the containers.
```console
$ docker run -dit --name unitcon prosyslab/unitcon-artifact bash
$ docker run -dit --name utbot prosyslab/unitcon-artifact-utbot bash
```

2. Check if the `unitcon` container is built well.
```console
$ docker exec -it unitcon bash
root@{sha}:~/unitcon# ocaml --version
The OCaml toplevel, version 4.13.1
root@{sha}:~/unitcon# java -version
openjdk version "1.8.0_442"
OpenJDK Runtime Environment (build 1.8.0_442-8u442-b06~us1-0ubuntu1~22.04-b06)
root@{sha}:~/unitcon# python3 --version
Python 3.10.12
```

3. Check if the `utbot` container is built well.
```console
$ docker exec -it utbot bash
root@{sha}:/usr/src/baselines# ls -l /usr/lib/jvm/
total 16
lrwxrwxrwx 1 root root   21 Jan 23 08:11 java-1.17.0-openjdk-amd64 -> java-17-openjdk-amd64
lrwxrwxrwx 1 root root   20 Jan 26 16:38 java-1.8.0-openjdk-amd64 -> java-8-openjdk-amd64
drwxr-xr-x 9 root root 4096 Apr 12 16:57 java-17-openjdk-amd64
drwxr-xr-x 7 root root 4096 Apr 12 16:57 java-8-openjdk-amd64
drwxr-xr-x 2 root root 4096 Apr 12 16:57 openjdk-17
lrwxrwxrwx 1 root root   15 Dec 19  2022 zulu11 -> zulu11-ca-amd64
drwxr-xr-x 9 root root 4096 Dec 19  2022 zulu11-ca-amd64
```

## __2. Directory structure__
```
├── baselines
│   ├── evofuzz
│   ├── evosuite
│   ├── npetest
│   ├── randoop
│   ├── utbot
│   └── utils.py
├── benchmark
│   ├── Javac
|   |   ├── ...   
|   |   └── chart-13
|   |       ├── ...
|   |       └── unitcon-properties
|   │           ├── build-command
|   |           ├── expected-bug
|   |           └── expected-bug-type
│   └── Maven
|       ├── ...   
|       └── math-100
|           ├── ...
|           └── unitcon-properties
|               ├── build-command
|               ├── expected-bug
|               └── expected-bug-type
├── Dockerfile
├── Dockerfile.utbot
├── Dockerfile.unknown.java11
├── Dockerfile.unknown.java17
├── Dockerfile.unknown.java21
├── Dockerfile.unknown.java8
├── README.md
├── resources
├── scripts
│   ├── build.py
│   ├── execute.py
│   ├── jar_maker.py
│   └── projects.json
├── unitcon-script
│   ├── build.py  
│   └── projects.json
├── unitcon
└── unknown-bug-bench
    ├── java11
    ├── java17
    ├── java21
    ├── java8
    ├── script
    └── tests
```

## __3. Reproducing the results in the paper__
### __3.1. Setup__
To build UnitCon, please run
```
cd unitcon
$ ./setup.sh
```

### __3.2. Running the experiment for RQ1__
If the following experiment are run sequentially, the total execution time is 125 days.

#### __Build__
Before starting the experiment, projects need to be built. You can run:
```
$ python3 script/execute.py build all
```

#### __UnitCon__
To run the experiment for UnitCon, you can run
```
$ python3 script/execute.py analyze all
$ python3 script/execute.py synthesize all --mode full --report unitcon-results
```

#### __Other tools (except UTBot)__
To run the experiment for other tools except UTBot, you can use the script `scripts/execute.py` as the following.
FYI, we used seed ranging from 1 to 10.
```
$ python3 execute.py all all --seed [seed] --timeout 10 --results results/[seed] --log /results/[seed].log
```

#### __UTBot__
UTBot can be executed using the Docker image `prosyslab/unitcon-artifact-utbot` for experimentation.
To run the experiment for UTBot, you can use the script `scripts/build.py` as the following.
```
$ cd baselines
$ python3 build.py all --log results/build.log
```

To run the experiment for UTBot, you can use the script `scripts/execute.py` as the following.
FYI, we used iteration ranging from 1 to 10.
```
$ python3 execute.py utbot all --timeout 10 --results results/[iteration] --log results/[iteration].log
```

### __3.3. Running the minimal version of 3.2__
Reproducing the experiments in our paper at a full scale will take a very long time with limited resources. This is because the full experiment would involve running 10 trials of 10 minutes each for all 198 projects. Thus, we provide a minimal version of the experiment that can be run in a reasonable amount of time. We reduced the number of projects to 20 and the number of repetitions to 3. Under the assumption of running the experiment sequentially, the total execution time is 4 days.

Before starting the experiment, projects need to be built. You can run:
```
$ python3 script/execute.py build minimal
```
#### __UnitCon__
To run the experiment for UnitCon, you can run
```
$ python3 script/execute.py analyze minimal
$ python3 script/execute.py synthesize minimal --mode full --report unitcon-results
```

#### __Other tools (except UTBot)__
To run the experiment for other tools except UTBot, you can use the script `scripts/execute.py` as the following.
you can use seed ranging from 1 to 3.
```
$ cd ~/unitcon
$ python3 execute.py all minimal --seed [seed] --timeout 10 --results results/[seed] --log results/[seed].log
```

#### __UTBot__
UTBot can be executed using the Docker image `prosyslab/unitcon-artifact-utbot` for experimentation.
To run the experiment for UTBot, you can use the script `scripts/build.py` as the following.
```
$ cd baselines
$ python3 build.py minimal --log results/build.log
```

To run the experiment for UTBot, you can use the script `scripts/execute.py` as the following.
You can use seed ranging from 1 to 3.
```
$ python3 execute.py utbot minimal --timeout 10 --results results/[iteration] --log results/[iteration].log
```

### __3.4. Running the experiment for RQ2__
For RQ2, we organized the setup so that experiments could be run using separate Docker images based on the Java version used to build each project.
To build a Docker image, you can run
```
docker build -t prosyslab/unitcon-unknown-bench:22.04-java[version] -f Dockerfile.unknown.java[version] .
```
TBD

### __3.5. Running the experiment for RQ3__
If the following experiment are run sequentially, the total execution time is 6 days.

#### __Build__
If RQ1 has not been executed, you can run
```
$ python3 script/execute.py build all
$ python3 script/execute.py analyze all
```

#### __UnitCon__
To run the experiment for UnitCon, you can run
```
$ python3 script/execute.py synthesize all --mode full --report unitcon-both-results
$ python3 script/execute.py synthesize all --mode priority --report unitcon-priority-results
$ python3 script/execute.py synthesize all --mode prune --report unitcon-prune-results
$ python3 script/execute.py synthesize all --mode basic --report unitcon-basic-results
```

### __3.6. Parsing the results__
TBD


## __4. The results of the experiments in the paper__
You can retrieve the results of the experiments in the paper from here.
Download the file `unitcon-experimental-result.tar.gz`, go to the `paper-script` directory and execute the script.

```
wget or download the archived file
tar -xvf unitcon-experimental-result.tar.gz
cd unitcon-experimental-result/paper-script
```

Then you can parse the results by running the following command to get the same results as in the paper.
```
python3 figure_[number]_[subfigure label].py
```

For example, to generate the chart shown Figure 6(a), you can run
```
python3 figure_6_a.py
```
