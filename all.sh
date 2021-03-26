#!/bin/bash

prev=logs/analyze
log=$prev/log
dir="$prev-$(date '+%y%m%d-%H:%M')"
glog="$dir/log"
mkdir -p $prev
mkdir -p $dir
rm -f $glog

echo "./run.sh $@"
echo "./run.sh $@" >> $glog

echo "update..."
git pull 2>> $glog 1>> $glog
git submodule update 2>> $glog 1>> $glog
echo "build project..."
sbt assembly 2>> $glog 1>> $glog
echo "run analyze..."

cd ecma262
git log > ../$dir/gitlog
cd ..

cat $dir/gitlog | while read line
do
  mkdir -p $prev
  rm -f $log
  case $line in
    commit*)
      version=${line:7}
      filename=$dir/$version
      echo "================================================================================" >> $glog
      echo "version: $version" >> $glog
      jiset analyze -parse:version=$version -log -analyze:target=.* $@ 2>> $log 1>> $log
      mv $prev $filename
      if [ -f "cfg.pdf" ]; then
        mv cfg.dot cfg_trans.dot cfg.pdf $dir/$VERSION
      fi
      echo "finished (check $filename)"
      ;;
  esac
done
