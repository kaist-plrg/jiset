#!/bin/bash

prev=logs/analyze
log=$prev/log
filename="$prev-$(date '+%y%m%d-%H:%M')"
mkdir -p $prev
rm -f $log

echo "update..."
git pull &>> $log
echo "build project..."
sbt assembly &>> $log
echo "run analyze..."
jiset analyze -log -analyze:target=.* &>> $log
mv $prev $filename
if [ -f "cfg.pdf" ]; then
  mv cfg.dot cfg_trans.dot cfg.pdf $filename
fi
echo "finished (check $filename)"
