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

## Overall Structure

![image](https://user-images.githubusercontent.com/6766660/124231185-e91d3380-db4a-11eb-95b5-dc43f4341ff2.png)

## Installation Guide

We explain how to install JISET with necessary environment settings from the
scratch.  Before installation, please download JDK 8 and
[`sbt`](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html).

### Download JISET
```
$ git clone https://github.com/kaist-plrg/jiset.git
$ cd jiset
```

### Environment Setting
Insert the following commands to `~/.bashrc` (or `~/.zshrc`):
```
# for JISET
export JISET_HOME="<path to JISET>"
export PATH="$JISET_HOME/bin:$PATH"
```
The `<path to JISET>` should be the absolute path of JISET repository.

## Basic Commands

You can run the artifact with the following command:
```
$ jiset <sub-command> <option>*
```
with the following sub-commands:
- `help` shows the help message.
- `extract` extracts ECMAScript model from `ecma262/spec.html`.
- `gen-model` generates ECMAScript models.
- `gen-tsmodel` generate ECMAScript models in TypeScript.
- `compile-repl` performs REPL for printing compile result of particular step.
- `gen-test` generates tests with the current implementation as the oracle.
- `parse` parses a JavaScript file using the generated parser.
- `load` loads a JavaScript AST to the initial IR states.
- `eval` evaluates a JavaScript file using generated interpreter.
- `parse-ir` parses an IR file.
- `load-ir` loads an IR AST to the initial IR states.
- `eval-ir` evaluates an IR file.
- `repl-ir` performs REPL for IR instructions.
- `build-cfg` builds control flow graph (CFG).

and global options:
- `-silent` does not show final results.
- `-debug` turns on the debug mode.
- `-interactive` turns on the interactive mode.
- `-no-bugfix` uses semantics including specification bugs.
- `-time` displays the duration time.
