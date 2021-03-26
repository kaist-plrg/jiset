#!/bin/bash

prev=logs/analyze
glog=logs/log
log=$prev/log
dir="$prev-$(date '+%y%m%d-%H:%M')"
mkdir -p $prev
rm -f $glog

echo "./run.sh $@"
echo "./run.sh $@" >> $glog

echo "update..."
git pull 2>> $glog 1>> $glog
git submodule update 2>> $glog 1>> $glog
echo "build project..."
sbt assembly 2>> $glog 1>> $glog
echo "run analyze..."

mkdir -p $dir

cd ecma262
git log | while read line
do
	cd ..
	mkdir -p $prev
	rm -f $log
	case $line in
		commit*)
			version=${line:7}
			filename=$dir/$version
			jiset analyze -parse:version=$version -log -analyze:target=.* $@ 2>> $log 1>> $log
			mv $prev $filename
			if [ -f "cfg.pdf" ]; then
				mv cfg.dot cfg_trans.dot cfg.pdf $dir/$VERSION
			fi
			echo "finished (check $filename)"
			;;
	esac
	cd ecma262
done

