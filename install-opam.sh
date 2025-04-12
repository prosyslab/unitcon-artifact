#!/bin/bash
apt install -y software-properties-common
add-apt-repository ppa:avsm/ppa -y
apt update -y
apt install -f
dpkg --configure -a
apt install -y opam