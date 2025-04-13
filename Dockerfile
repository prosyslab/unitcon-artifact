FROM ubuntu:22.04

ENV TZ=Asia/Seoul
ENV LC_ALL=C.UTF-8
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ >/etc/timezone

RUN apt update && \
    apt install -y python3 python3-pip libpython3.10 python3.10-venv openjdk-8-jdk && \
    apt install -y wget unzip maven make time git vim tzdata sqlite3
RUN echo "alias time='/usr/bin/time'" >> /root/.bashrc
RUN pip3 install pandas matplotlib venn

COPY ./install-opam.sh /root
RUN /root/install-opam.sh

ENV BENCH_HOME=/opt/benchmarks
ADD ./resources/* /root/
COPY ./benchmark $BENCH_HOME

WORKDIR $BENCH_HOME
RUN bash fix_bench.sh

ENV UNITCON_HOME=/root/unitcon
COPY ./scripts $UNITCON_HOME

WORKDIR $UNITCON_HOME
RUN git config --global url."https://github.com/".insteadOf "git@github.com:"
RUN git clone https://github.com/prosyslab/unitcon.git --recursive

ENV UNITCON_TOOL_HOME=$UNITCON_HOME/unitcon
COPY ./unitcon-script $UNITCON_TOOL_HOME/script

ENV TOOLS_HOME=$UNITCON_HOME/baselines
COPY ./baselines $TOOLS_HOME

ENV RANDOOP_HOME=$TOOLS_HOME/randoop
WORKDIR $RANDOOP_HOME
RUN bash download_and_extract.sh

ENV EVOSUITE_HOME=$TOOLS_HOME/evosuite
WORKDIR $EVOSUITE_HOME
RUN bash download.sh

ENV OPAMCONFIRMLEVEL=unsafe-yes
WORKDIR $UNITCON_HOME
