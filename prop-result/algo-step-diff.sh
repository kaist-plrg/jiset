#!/bin/bash

prop_list="\
  prop_matchAll \
  prop_globalThis \
  prop_nulllish_coalescing \
  prop_Promise_allSettled \
  prop_optional_chaining \
  prop_BigInt \
  prop_dynamic_import \
  prop_import_meta \
"

for prop in $prop_list
do
  git checkout origin/$prop
  git submodule update --recursive > /dev/null
  echo "[$prop] clean & compile..."
  sbt clean compile 2>&1 >> /dev/null
  echo "[$prop] completed!"
  echo "[$prop] run algo-step-diff..."
  sbt "run algo-step-diff"
done

git checkout master
git submodule update --recursive
