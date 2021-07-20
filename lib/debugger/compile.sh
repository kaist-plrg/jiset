#!/bin/bash
SBT_FILE=$JISET_HOME/build.sbt
SBT_TEMP_FILE=$JISET_HOME/build.sbt.tmp
JS_FILE=$JISET_HOME/target/scala-2.13/jiset-fastopt/main.js
DEBUGGER_LIB_PATH=$JISET_HOME/lib/debugger/src/lib/jiset.js

cd $JISET_HOME
mv $SBT_FILE $SBT_TEMP_FILE
{ echo 'enablePlugins(ScalaJSPlugin)'; echo 'scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }'; cat $SBT_TEMP_FILE; } > $SBT_FILE
sbt fastLinkJS
cp $JS_FILE $DEBUGGER_LIB_PATH
mv $SBT_TEMP_FILE $SBT_FILE
sbt assembly
