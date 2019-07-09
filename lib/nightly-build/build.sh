#!/bin/bash
# run at crontab. ex) 0 0 * * * /home/j31d0/ase/lib/nightly-build/build.sh
logNAME=/home/j31d0/ase/$(date +"%Y%m%d%H%M%S").log
export ASE_HOME=/home/j31d0/ase/
(cd /home/j31d0/ase/ && git pull && sbt clean generateModel test262Test) 2> $logNAME
[ -s $logNAME ] || rm $logNAME
