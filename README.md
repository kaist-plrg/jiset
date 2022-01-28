# JISET: JavaScript IR-based Semantics Extraction Toolchain

**JISET** is a **J**avaScript **I**R-based **S**emantics **E**xtraction
**T**oolchain. It is the first tool that automatically synthesizes parsers and
AST-IR translators directly from a given language specification, ECMAScript.

## Publications

Details of the JISET framework are available in our papers:
- [ASE 2020] [JISET: JavaScript IR-based Semantics Extraction
  Toolchain](https://doi.org/10.1145/3324884.3416632)
- [ICSE 2021] [JEST: N+1-version Differential Testing of Both JavaScript
  Engines](https://doi.org/10.1109/ICSE43902.2021.00015)
- [ASE 2021] [JSTAR: JavaScript Specification Type Analyzer using
  Refinement](https://ieeexplore.ieee.org/document/9678781)

## Overall Structure

![image](https://user-images.githubusercontent.com/6766660/124231185-e91d3380-db4a-11eb-95b5-dc43f4341ff2.png)

## Installation Guide

We explain how to install JISET with necessary environment settings from
scratch.  Before installation, please download JDK 8 and
[`sbt`](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html).

### Download JISET
```bash
$ git clone https://github.com/kaist-plrg/jiset.git
```

### Environment Settings
Insert the following commands to `~/.bashrc` (or `~/.zshrc`):
```bash
# for JISET
export JISET_HOME="<path to JISET>" # IMPORTANT!!!
export PATH="$JISET_HOME/bin:$PATH" # for executables `jiset` and etc.
source $JISET_HOME/jiset-auto-completion # for auto completion
```
The `<path to JISET>` should be the absolute path of the JISET repository.

### Installation of JISET using `sbt`
```bash
$ cd jiset && git submodule init && git submodule update && sbt assembly
```

## Simple Examples
Extract and generate a model from ECMAScript 2021 (ES12 / es2021):
```bash
$ jiset gen-model -extract:version=es2021
# ========================================
#  extract phase
# ----------------------------------------
# version: es2021
# parsing spec.html... (10,476 ms)
# ========================================
#  gen-model phase
# ----------------------------------------
# generating models... (240 ms)
```
Create the following JavaScript file `sample.js`:
```js
// sample.js
var x = 1 + 2;
print(x);
```
Parse `sample.js`:
```bash
$ jiset parse sample.js
# ========================================
#  parse phase
# ----------------------------------------
# var x = 1 + 2 ; print ( x ) ;
```
Evaluate `sample.js`:
```bash
$ jiset eval sample.js -silent
# 3.0
```
Show the details and final results of the evaluation of `sample.js`:
```bash
$ jiset eval sample.js -debug
```

## Basic Commands

You can run the artifact with the following command:
```bash
$ jiset <sub-command> <option>*
```
with the following sub-commands:
- `help` shows the help message.
- `extract` extracts an ECMAScript model from `ecma262/spec.html`.
- `gen-model` generates ECMAScript models.
- `compile-repl` performs REPL for printing the compile result of a particular step.
- `gen-test` generates tests with the current implementation as the oracle.
- `parse` parses a JavaScript file using the generated parser.
- `load` loads a JavaScript AST to the initial IR states.
- `eval` evaluates a JavaScript file using the generated interpreter.
- `filter-meta` extracts and filters out metadata of test262 tests.
- `parse-ir` parses an IR file.
- `load-ir` loads an IR AST to the initial IR states.
- `eval-ir` evaluates an IR file.
- `repl-ir` performs REPL for IR instructions.
- `build-cfg` builds control flow graphs (CFGs).

and global options:
- `-silent` does not show final results.
- `-debug` turns on the debug mode.
- `-interactive` turns on the interactive mode.
- `-no-bugfix` uses the semantics including specification bugs.
- `-time` displays the duration time.

## ECMAScript Debugger Guide

**ECMAScript Debugger** extends the interpreter of JISET to help you understand how a JavaScript Program runs in ECMAScript.
Currently, it is in an **alpha stage** and supports only basic features such as:

- Step-by-step execution of ECMAScript
- Breakpoints by abstract algorithm names in ECMAScript
- Visualization of states like a call stack, an environment, and a heap of ECMAScript
- Line-by-line execution of JavaScript

A short [introduction video](https://youtu.be/syfZ3v6JNg8) is available.

### How to Run

You can try ECMAScript Debugger with the following instructions in the `$JISET_HOME` directory:

First, you should check out to the `editor` branch to use ECMAScript Debugger.

```bash
$ git checkout editor && git submodule update && sbt assembly
```

Next, you should start a server.

```bash
$ jiset web
```

Finally, start a debugger client.

```bash
$ cd web && npm install && npm start
```

### Future Plans

We have the following future plans:
- Add more debugger features:
  - Show a JavaScript state by refining an ECMAScript state.
  - Record timestamps during execution for resume & suspend steps (especially for Generator).
  - ...
- Show relevant test262 tests for each algorithm step in the specification viewer.
- Show the type of each variable using the type analysis result of JSTAR.
- Live-edit of "spec.html" in the specification viewer.
