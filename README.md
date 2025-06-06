# UnitCon: Synthesizing Targeted Unit Tests for Java Runtime Exceptions (Paper Artifact)

This is the artifact of the paper *UnitCon: Synthesizing Targeted Unit Tests for Java Runtime Exceptions* to appear in FSE 2025.

## __1. Directory structure__
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
    ├── java21
    ├── java8
    ├── script
    └── tests
```

## __2. Getting started__
### __2.1. System requirements__
To run the experiments in the paper, we used Linux machines with 512GB RAM and Intel Xeon 2.90GHz.
The experiment requires **188** GB storage for Docker images and experimental results.

Additionally, we assume that the following environment settings are met.
- Ubuntu 22.04
- python 3.8+
- pip3
- Docker

### __2.2. Preparing the Docker image__
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

Please download `m2-cache.tar` from the **[here](https://zenodo.org/records/15460355)** path, place it inside the `resources` directory, and then run the following command to build the image.
```
$ docker build -t prosyslab/unitcon-artifact -f Dockerfile .
$ docker build -t prosyslab/unitcon-artifact-utbot -f Dockerfile.utbot .
```

Both commands take about **10** minutes to build each docker image, respectively.

### __2.3. Start Docker containers and check their validity__
1. Start the containers.
```console
$ docker run -dit --name unitcon prosyslab/unitcon-artifact bash
$ docker run -dit --name utbot prosyslab/unitcon-artifact-utbot bash
```

2. Check if the `unitcon` container is built well.
```console
$ docker exec -it unitcon bash
root:~/unitcon# ocaml --version
The OCaml toplevel, version 4.13.1
root:~/unitcon# java -version
openjdk version "1.8.0_442"
OpenJDK Runtime Environment (build 1.8.0_442-8u442-b06~us1-0ubuntu1~22.04-b06)
root:~/unitcon# python3 --version
Python 3.10.12
```

3. Check if the `utbot` container is built well.
```console
$ docker exec -it utbot bash
root:/usr/src/baselines# ls -l /usr/lib/jvm/
total 16
lrwxrwxrwx 1 root root   21 Jan 23 08:11 java-1.17.0-openjdk-amd64 -> java-17-openjdk-amd64
lrwxrwxrwx 1 root root   20 Jan 26 16:38 java-1.8.0-openjdk-amd64 -> java-8-openjdk-amd64
drwxr-xr-x 9 root root 4096 Apr 12 16:57 java-17-openjdk-amd64
drwxr-xr-x 7 root root 4096 Apr 12 16:57 java-8-openjdk-amd64
drwxr-xr-x 2 root root 4096 Apr 12 16:57 openjdk-17
lrwxrwxrwx 1 root root   15 Dec 19  2022 zulu11 -> zulu11-ca-amd64
drwxr-xr-x 9 root root 4096 Dec 19  2022 zulu11-ca-amd64
```

-----

## __3. Reproducing the results for RQ1__

### __3.1. Running the experiment (Full)__
If you run the following experiment sequentially, it take approximately 125 days in total.
If you run the tools in parallel, it takes **33** hours for UnitCon and **330** hours for the other tools.
Alternatively, we provide a minimal set of experiments to check the consistency between this artifact and the paper in the next section (3.3).
It will take **2** days.

#### __Build__
First, we need to build the target programs. Please run the following command in the `unitcon` container:
```console
root:~/unitcon# cd unitcon/
root:~/unitcon/unitcon# python3 script/execute.py build all
build started: avro_a7a43da
build done: avro_a7a43da
...
all build done!
root:~/unitcon/unitcon# cd ..
root:~/unitcon# python3 build.py all --log build.log
04/13/2025 12:12:31 - INFO - __main__ - **********
04/13/2025 12:12:31 - INFO - __main__ - build started: activemq-artemis_6fbafc4
...
```

#### __UnitCon__
We can execute UnitCon with the following commands in the `unitcon` container:
```console
root:~/unitcon# cd unitcon
root:~/unitcon/unitcon# python3 script/execute.py analyze all
analysis started: avro_a7a43da
analysis done: avro_a7a43da
...
root:~/unitcon/unitcon# python3 script/execute.py synthesize all --mode full --report unitcon-results
synthesis started: avro_a7a43da
synthesis done: avro_a7a43da
...
```

#### __Other tools (except UTBot)__
To run the experiment for other tools except UTBot, you can use the script `scripts/execute.py` as the following.
FYI, we used seed ranging from 1 to 10.
```console
root:~/unitcon# python3 execute.py all all --seed [seed] --timeout 10 --results results/[seed] --log results/[seed].log
root:~/unitcon# python3 execute.py all all --seed [seed] --timeout 10 --results results/[seed] --target_method --log results/[seed].log
```
Example:
```console
root:~/unitcon# python3 execute.py all all --seed 1 --timeout 10 --results results/1 --log results/1.log
04/13/2025 12:41:06 - INFO - randoop.execute - classpath: with_dependency.jar
04/13/2025 12:41:06 - INFO - randoop.execute - execute randoop project: avro_a7a43da
...
```

(Optional) If you would like to execute a single tool, please use the following command:
```console
root:~/unitcon# python3 execute.py [tool] all --seed [seed] --timeout 10 --results results/[seed] --log results/[seed].log
```

(Optional) If you would like to execute tools on a single target project, please use the following command:
```console
root:~/unitcon# python3 execute.py all [project] --seed [seed] --timeout 10 --results results/[seed] --log results/[seed].log
```

Example:
```console
root@b60bdb700f49:~/unitcon# python3 execute.py evofuzz avro_a7a43da --seed 1 --timeout 10 --results results/1 --log results/1.log
04/13/2025 12:44:53 - INFO - evofuzz.execute - classpath: with_dependency.jar
04/13/2025 12:44:53 - INFO - evofuzz.execute - execute evofuzz project: avro_a7a43da
...
```

#### __UTBot__
UTBot can be executed using the Docker image `prosyslab/unitcon-artifact-utbot`.
Please start a shell in the `utbot` container that you've created at the section 2.2, and execute the following commands:

1. Build the target projects
```console
root:/usr/src/baselines# python3 build.py all --log build.log
04/13/2025 12:12:31 - INFO - __main__ - **********
04/13/2025 12:12:31 - INFO - __main__ - build started: activemq-artemis_6fbafc4
...
```
2. Execute the tool UTBot. FYI, we used iteration ranging from 1 to 10.
```console
root:/usr/src/baselines# python3 execute.py utbot all --timeout 10 --results results/[iteration] --log results/[iteration].log
```
Example:
```console
root:/usr/src/baselines# python3 execute.py utbot all --timeout 10 --results results/1 --log results/1.log
04/13/2025 12:21:16 - INFO - utbot.execute - classpath: with_dependency.jar
04/13/2025 12:21:16 - INFO - utbot.execute - execute utbot project: activemq-artemis_6fbafc4
...
```

### __3.2. Running the minimal version of 3.2 (Optional)__
Reproducing the experiments in our paper at a full scale will take a very long time with limited resources. This is because the full experiment would involve running 10 trials of 10 minutes each for all 198 projects. Thus, we provide a minimal version of the experiment that can be run in a reasonable amount of time. We reduced the number of projects to 8 and the number of repetitions to 3. Under the assumption of running the experiment sequentially, the total execution time is 2 days.

#### __Build__
First, we need to build the target programs. Please run the following command in the `unitcon` container:
```console
root:~/unitcon# cd unitcon/
root:~/unitcon/unitcon# python3 script/execute.py build minimal
build started: Bears-196-buggy
build done: Bears-196-buggy
...
all build done!
root:~/unitcon/unitcon# cd ..
root:~/unitcon# python3 build.py all --log build.log
04/13/2025 12:56:24 - INFO - __main__ - **********
04/13/2025 12:56:24 - INFO - __main__ - build started: Bears-196-buggy
...
```


#### __UnitCon__
We can execute UnitCon with the following commands in the `unitcon` container:
```console
root:~/unitcon# cd unitcon/
root:~/unitcon/unitcon# python3 script/execute.py analyze minimal
analysis started: Bears-196-buggy
...
all analysis done!
root:~/unitcon/unitcon# python3 script/execute.py synthesize minimal --mode full --report unitcon-results
synthesis started: Bears-196-buggy
...
```

#### __Other tools (except UTBot)__
To run the experiment for other tools except UTBot, you can use the script `execute.py` as the following.
you can use seed ranging from 1 to 3.
```console
root:~/unitcon# python3 execute.py all minimal --seed 1 --timeout 10 --results results/1 --log results/1.log
04/13/2025 12:58:28 - INFO - randoop.execute - classpath: with_dependency.jar
04/13/2025 12:58:28 - INFO - randoop.execute - execute randoop project: Bears-196-buggy
...
```

Also, please execute the same tools with `--target_method` option:
```console
root:~/unitcon# python3 execute.py all minimal --seed 1 --timeout 10 --results results/1 --target_method --log results/1.log
04/13/2025 15:37:07 - INFO - randoop.execute - classpath: with_dependency.jar
04/13/2025 15:37:07 - INFO - randoop.execute - execute randoop project: Bears-196-buggy
...
```

#### __UTBot__
UTBot can be executed using the Docker image `prosyslab/unitcon-artifact-utbot`.
Please start a shell in the `utbot` container that you've created at the section 2.2, and execute the following commands:
```console
root:/usr/src/baselines# python3 build.py minimal --log build.log
04/13/2025 12:56:24 - INFO - __main__ - **********
04/13/2025 12:56:24 - INFO - __main__ - build started: Bears-196-buggy
...
```

To run the experiment for UTBot, you can use the script `scripts/execute.py` as the following.
You can use seed ranging from 1 to 3.
```console
root:/usr/src/baselines# python3 execute.py utbot minimal --timeout 10 --results results/[iteration] --log results/[iteration].log
```

Example:
```console
root:/usr/src/baselines# python3 execute.py utbot minimal --timeout 10 --results results/1 --log results/1.log
04/13/2025 13:06:05 - INFO - utbot.execute - classpath: with_dependency.jar
04/13/2025 13:06:05 - INFO - utbot.execute - execute utbot project: Bears-196-buggy
...
```

-----
## __4. Reproducing the results for RQ2 (Optional)__

### __4.1. Setup__
For RQ2, we organized the setup so that experiments could be run using separate Docker images based on the Java version used to build each project.

The projects where you can check the reported bugs for each benchmark are listed below.
- **java 8**
    - commons-configuration_37f5448
    - commons-dbcp_6ce68bf
    - commons-io_9d8a87c
    - JSqlParser_67e2204
- **java 11**
    - johnzon_885cec0
    - karaf_fb612cf
    - nutz_17c2dbc
    - opengrok_b20ecb2
- **java 21**
    - Activiti_17918a5
    - client-java_da90f87
    - commons-bcel_f15e858
    - commons-math_cba0440
    - feign_903a5d7
    - pdfbox_6f8ebc1
    - tsfile_6fcc23


1. Build Docker images
```console
$ docker build -t prosyslab/unitcon-unknown-bench:java8 -f Dockerfile.unknown.java8 .
$ docker build -t prosyslab/unitcon-unknown-bench:java11 -f Dockerfile.unknown.java11 .
$ docker build -t prosyslab/unitcon-unknown-bench:java21 -f Dockerfile.unknown.java21 .
```

2. Start containers
```console
$ docker run -dit --name unitcon-rq2-java8 prosyslab/unitcon-unknown-bench:java8 bash
$ docker run -dit --name unitcon-rq2-java11 prosyslab/unitcon-unknown-bench:java11 bash
$ docker run -dit --name unitcon-rq2-java21 prosyslab/unitcon-unknown-bench:java21 bash
```

### __4.2. Execute UnitCon__

Plrease run the following command in the corresponding Java version container to generate test cases that reproduce the reported bugs.
```console
$ docker exec -it unitcon-rq2-java[version] bash
root:~/unitcon/unitcon# python3 script/execute.py [project name]
```

For example, if you want to reproduce the bugs of `commons-dbcp_6ce68bf` in Java 8 container, you can run
```console
$ docker exec -it unitcon-rq2-java8 bash
root:~/unitcon/unitcon# python3 script/execute.py commons-dbcp_6ce68bf
```

If you want to reproduce all the reported bugs in a single container, simply run
```console
$ docker exec -it unitcon-rq2-java[version] bash
root:~/unitcon/unitcon# python3 script/execute.py all
```

The test cases generated by following the steps above can be found at `~/unitcon/results/[project name]`.

For example, if you have generated test case for the `commons-dbcp_6ce68bf` project, you can see the file `~/unitcon/results/commons-dbcp_6ce68bf/commons-dbcp_6ce68bf/UnitconTest1.java`:
```java
import java.sql.DatabaseMetaData;
import org.apache.commons.dbcp2.DelegatingConnection;
import org.apache.commons.dbcp2.DelegatingDatabaseMetaData;
import org.junit.Test;

/* Duration of synthesis: 21.9514689445 */
public class UnitconTest1 {
@Test
public void test() {
try {
String catalog1 = null;
DelegatingConnection connection4 = null;
DatabaseMetaData databaseMetaData9 = null;
DelegatingDatabaseMetaData con_recv5 = new DelegatingDatabaseMetaData(connection4, databaseMetaData9);
DatabaseMetaData databaseMetaData5 = con_recv5.getInnermostDelegate();
DelegatingDatabaseMetaData con_recv1 = new DelegatingDatabaseMetaData(connection4, databaseMetaData5);
con_recv1.getFunctions(catalog1, catalog1, catalog1);
}
catch(Exception e) {
e.printStackTrace();
}
}
}
```

Note that you can check the test cases mentioned in paper at `/opt/benchmarks/tests/[project]` inside each container.

-----

## __5. Reproducing the results for RQ3__

### __5.1. Running the experiment (Full)__
If the following experiment are run sequentially, the total execution time is 6 days.

#### __Build__
If RQ1 has not been executed, please setup the target programs first:
```console
$ docker exec -it unitcon bash
root:~/unitcon# cd unitcon/
root:~/unitcon/unitcon# python3 script/execute.py build all
root:~/unitcon/unitcon# python3 script/execute.py analyze all
```

#### __UnitCon__
UnitCon provide 4 `mode` to support the combination of 2 search strategies, `priority` and `prune`.
Please use the following commands to execute UnitCon with the different search strategies:
```console
root:~/unitcon/unitcon# python3 script/execute.py synthesize all --mode full --report unitcon-both-results
root:~/unitcon/unitcon# python3 script/execute.py synthesize all --mode priority --report unitcon-priority-results
root:~/unitcon/unitcon# python3 script/execute.py synthesize all --mode prune --report unitcon-prune-results
root:~/unitcon/unitcon# python3 script/execute.py synthesize all --mode basic --report unitcon-basic-results
```

### __5.2. Running the minimal version of 5.2 (Optional)__
Reproducing the experiments in our paper at a full scale will take a very long time with limited resources. This is because the full experiment would involve running 4 trials of 10 minutes each for all 198 projects. Thus, we provide a minimal version of the experiment that can be run in a reasonable amount of time. We reduced the number of projects to 8. Under the assumption of running the experiment sequentially, the total execution time is 5 hours.

#### __Build__
If minimal version of RQ1 has not been executed, you can run
```console
$ docker exec -it unitcon bash
root:~/unitcon# cd unitcon/
root:~/unitcon/unitcon# python3 script/execute.py build minimal
root:~/unitcon/unitcon# python3 script/execute.py analyze minimal
```

#### __UnitCon__
To run the experiment for UnitCon, you can run
```console
root:~/unitcon/unitcon# python3 script/execute.py synthesize minimal --mode full --report unitcon-both-results
root:~/unitcon/unitcon# python3 script/execute.py synthesize minimal --mode priority --report unitcon-priority-results
root:~/unitcon/unitcon# python3 script/execute.py synthesize minimal --mode prune --report unitcon-prune-results
root:~/unitcon/unitcon# python3 script/execute.py synthesize minimal --mode basic --report unitcon-basic-results
```

## __6. Plotting the results__
#### __Init Structure__
After all the above experiments have finished, the results will be stored in both containers following the directory structure shown below.

1. `~/unitcon/results` directory of `unitcon` container
```
├── unitcon-results
│   ├── ...
│   └── unitcon-report.csv
├── unitcon-both-results
│   ├── ...
│   └── unitcon-report.csv
├── unitcon-priority-results
│   ├── ...
│   └── unitcon-report.csv
├── unitcon-prune-results
│   ├── ...
│   └── unitcon-report.csv
├── unitcon-basic-results
│   ├── ...
│   └── unitcon-report.csv
├── 1
│   ├── evofuzz
│   │   ├── ...
│   │   └── evofuzz.csv
│   ├── evofuzz-method
│   │   ├── ...
│   │   └── evofuzz-method.csv
│   ├── evosuite
│   │   ├── ...
│   │   └── evosuite.csv
│   ├── evosuite-method
│   │   ├── ...
│   │   └── evosuite-method.csv
│   ├── npetest
│   │   ├── ...
│   │   └── npetest.csv
│   ├── npetest-method
│   │   ├── ...
│   │   └── npetest-method.csv
│   ├── randoop
│   │   ├── ...
│   │   └── randoop.csv
│   ├── randoop-method
│   │   ├── ...
│   │   └── randoop-method.csv
│   └── ...
└── ...
```

2. `/usr/src/baselines/results` directory of `utbot` container
```
├── 1
│   └── utbot
│       ├── ...
│       └── utbot.csv
└── ...
```

#### __Combine Structure__
Please copy the data from the `utbot` container to the `unitcon` container as follows:
```console
$ docker cp utbot:/usr/src/baselines/results ./results
Successfully copied 71.8MB to /pass/to/unitcon-artifact/results
$ docker cp results/1/utbot unitcon:/root/unitcon/results/1/utbot
Successfully copied 24.1MB to unitcon:/root/unitcon/results/1/utbot
$ docker cp results/2/utbot unitcon:/root/unitcon/results/2/utbot
Successfully copied 24.1MB to unitcon:/root/unitcon/results/2/utbot
$ docker cp results/3/utbot unitcon:/root/unitcon/results/3/utbot
Successfully copied 22.5MB to unitcon:/root/unitcon/results/3/utbot
...
```
Repeat the copy command for the number of iterations you've executed UTBot.

Then the merged `results` directory is structured as shown below:
```
├── unitcon-results
│   ├── ...
│   └── unitcon-report.csv
├── unitcon-both-results
│   ├── ...
│   └── unitcon-report.csv
├── unitcon-priority-results
│   ├── ...
│   └── unitcon-report.csv
├── unitcon-prune-results
│   ├── ...
│   └── unitcon-report.csv
├── unitcon-basic-results
│   ├── ...
│   └── unitcon-report.csv
├── 1
│   ├── evofuzz
│   │   ├── ...
│   │   └── evofuzz.csv
│   ├── evofuzz-method
│   │   ├── ...
│   │   └── evofuzz-method.csv
│   ├── evosuite
│   │   ├── ...
│   │   └── evosuite.csv
│   ├── evosuite-method
│   │   ├── ...
│   │   └── evosuite-method.csv
│   ├── npetest
│   │   ├── ...
│   │   └── npetest.csv
│   ├── npetest-method
│   │   ├── ...
│   │   └── npetest-method.csv
│   ├── randoop
│   │   ├── ...
│   │   └── randoop.csv
│   ├── randoop-method
│   │   ├── ...
│   │   └── randoop-method.csv
│   ├── utbot
│   │   ├── ...
│   │   └── utbot.csv
│   └── ...
└── ...
```

#### __Running Script__
After the experimental results have been structured as shown above, you can run the following scripts to prepare the data for plotting and to generate the graphs.

1. Start a shell in the `unitcon` container and change the working directory
```console
$ docker exec -it unitcon bash
root:~/unitcon# cd plot-script/
```

2. Prepare the data for plotting.
* RQ1. If you experimented full version of RQ1 in Section 3.2, `[iteration]` would be 10. If you used `minimal` option in Section 3.3, `[iteration]` would be 3.
```console
root:~/unitcon/plot-script# python3 modify_csv.py rq1 --iteration [iteration]
```
* RQ3
```console
root:~/unitcon/plot-script# python3 modify_csv.py rq3
```

3. Generate the figures
* Figure 6
```console
root:~/unitcon/plot-script# python3 figure_6_a.py --iteration [iteration]
root:~/unitcon/plot-script# python3 figure_6_b.py
```
* Figure 7
```console
root:~/unitcon/plot-script# python3 figure_7_a.py --iteration [iteration]
root:~/unitcon/plot-script# python3 figure_7_b.py --iteration [iteration]
```
* Figure 9
```console
root:~/unitcon/plot-script# python3 figure_9_a.py
root:~/unitcon/plot-script# python3 figure_9_b.py
```


## __7. The results of the experiments in the paper__
You can retrieve the results of the experiments in the paper from **[here](https://zenodo.org/records/15460355)**.
Download the file `unitcon-experimental-result.tar.gz`, go to the `paper-script` directory and execute the script.

```console
$ docker exec -it unitcon bash
root:~/unitcon# wget https://zenodo.org/records/15460355/files/unitcon-experimental-result.tar.gz
root:~/unitcon# tar -xvf unitcon-experimental-result.tar.gz
root:~/unitcon# cd unitcon-experimental-result/paper-script
```

Then you can parse the results by running the following command to get the same results as in the paper.
```console
root:~/unitcon/unitcon-experimental-result/paper-script# python3 figure_[number]_[subfigure label].py
```

For example, to generate the chart shown Figure 6(a), you can run
```console
root:~/unitcon/unitcon-experimental-result/paper-script# python3 figure_6_a.py
```
