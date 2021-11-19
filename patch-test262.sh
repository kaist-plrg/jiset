#!/bin/bash

echo $#
if [ $# -ne "0" ];then
  cd tests/test262 && git checkout -- .
  echo "Revert test262 patch"
else
  cd tests/test262 && patch -p1 < ../../test262.diff
  echo "Apply test262 patch"
fi
