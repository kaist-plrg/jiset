#!/bin/bash

prev=logs/analyze
log=$prev/log
filename="$prev_$(date '+%y%m%d_%I:%M')"
mkdir -p $prev
rm -f $log

echo "update..."
git pull &>> $log
echo "build project..."
sbt assembly &>> $log
echo "run analyze..."
jiset analyze -log -analyze:target=.* &>> $log
mv $prev $filename
echo "finished (check $filename)"
