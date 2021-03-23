#!/bin/bash

prev=logs/analyze
log=$prev/log
filename="$prev-$(date '+%y%m%d-%H:%M')"
mkdir -p $prev
rm -f $log

echo "./run.sh $@"
echo "./run.sh $@" >> $log

echo "update..."
git pull 2>> $log 1>> $log
git submodule update 2>> $log 1>> $log
echo "build project..."
sbt assembly 2>> $log 1>> $log
echo "run analyze..."
jiset analyze -log -analyze:target=.* $@ 2>> $log 1>> $log
mv $prev $filename
if [ -f "cfg.pdf" ]; then
  mv cfg.dot cfg_trans.dot cfg.pdf $filename
fi
echo "finished (check $filename)"
