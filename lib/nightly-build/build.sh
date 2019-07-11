#!/bin/bash
# run at crontab. ex) 0 0 * * * /home/j31d0/ase/lib/nightly-build/build.sh
BASE=/home/j31d0/ase/
logNAME=$BASE/$(date +"%Y%m%d%H%M%S").log
export ASE_HOME=$BASE
(cd $BASE && git pull && sbt clean generateModel test262Test) 2> $logNAME
sed "/Run completed in/q" $BASE/tests/detail | grep -A1 --no-group-separator "\*\*\* FAILED \*\*\*" | grep -v "\*\*\* FAILED \*\*\*" | sed -r "s/\x1B\[([0-9]{1,2}(;[0-9]{1,2})?)?[mGK]//g" | sort | uniq -c | sort -rg -k 1 > $BASE/tests/result/errlist_$(date +"%Y%m%d").log
[ -s $logNAME ] || rm $logNAME
