#!/bin/bash
# run at crontab. ex) 0 0 * * * /home/j31d0/jiset/lib/nightly-build/build.sh
BJISET=/home/j31d0/jiset/
logNAME=$BJISET/$(date +"%Y%m%d%H%M%S").log
export JISET_HOME=$BJISET
(cd $BJISET && git pull && sbt clean generateModel test262Test) 2> $logNAME
sed "/Run completed in/q" $BJISET/tests/detail | grep -A1 --no-group-separator "\*\*\* FAILED \*\*\*" | grep -v "\*\*\* FAILED \*\*\*" | sed -r "s/\x1B\[([0-9]{1,2}(;[0-9]{1,2})?)?[mGK]//g" | sort | uniq -c | sort -rg -k 1 > $BJISET/tests/result/errlist_$(date +"%Y%m%d").log
[ -s $logNAME ] || rm $logNAME
