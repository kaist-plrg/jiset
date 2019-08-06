# ASE
Automatic Semantics Extraction

## Install
```
git clone --recurse-submodules git@github.com:kaist-plrg/ase.git
sbt generateModel
```

## Unsupported Features
- Parts of built-in objects
  - Date
  - Math
  - JSON
  - RegExp
- module
- strict mode
- [[CanBlock]]
- locale
- Async/Generator

# Outline for ICSE 2020

## Introduction
- JavaScript is a essential programming language in client-sides.
- Because of its complicated semantics, it is not easy to understand JavaScript program behaviors.
- Several exisiting proposed formal semantics of JavaScript, but these have the following limitations.
  - Fully manual implementation of parsers and interpreters.
  - Supported only fixed version of ECMAScript (in most cases, ES 5.1).
- We propose a way to automatically extract semantics from ECMAScript specifications and deal with its annual updates.

## Motivation
- Sytnax: automatically generate parser from ES-BNF
  - ES-BNF features
  - Packrat Parsers with 1-lookahead
  - Example
- Semantics: automatically convert abstract algorithms into Core programs
  - Abstract algorithms
  - Example

## Overall Structure
- Figure of overall structure
- Explanation of each module

## Parser Generation
- Predictive parsers vs recursive descent parsers
  - Predictive parser
    - PROS: widely-used (Yacc), linear time, lookahead for predictions, no backtrack
    - CONS: need to restructure CFG, not easy to implement ES-BNF features
  - Recursive descent parsers
    - PROS: Scala-supported (Scala parser combinators), easily support ES-BNF features, no need to re-structure CFG
    - CONS: PEG not CFG, exponential time for bactkracing. 
- Packrat parser + 1 lookahaed
  - combination of recursive descent parsers with predictive parsers
  - implicit lookaheads
- Converting ES-BNF into Parsers
  - How to handle each ES-BNF features

## Compilation from Algorithm into Core
- Formal definition of Core language
  - Syntax
  - Semantics
- Abstract algorithms
  - syntax-directed / internal method
  - Converted into Core

## Evaluation
- RQ1) Does ASE correctly extract semantics?
- RQ2) Is endurable size of manual portion in implementation of ASE?
- RQ3) Does ASE deal with annual updates from small changes?
- RQ4) How many specification errors are detected by ASE?

## Related Work
- LambdaJS
- KJS

## Conclusion


# Potential Related Works
- Automatic Model Generation from Documentation for Java API Functions (ICSE'16)
- KJS: A complete formal semantics of JavaScript (PLDI'15)
- The Essence of JavaScript (ECOOP'10)
- A Trusted Mechanised JavaScript Specification (POPL'14)

# TODO
- [NotSupported]
  - Regex / Date / Math
  - Generator / Async
  - Module
  - Strict
- [NotYetImpl]
- Parser refactoring
- Rule 가다듬기
- Builtin objects 자동화
- Spec Extraction: JavaScript -> Scala?
- Spec Extraction: not yet
- Issues
- Parser 안되는 부분 (e.g. Semicolon insertion 부족한 부분)
- Evaluation
  - Taint analysis?
  - Dynamic slicing?
  - Error reporting / 정리
- Parser Related Work 


# Reference

git submodule reference at the time of Jul 20, 2019

[ECMA262](https://github.com/tc39/ecma262/tree/a68d1296f156ff73075fde36aebd643de4f8ebde)

[test262](https://github.com/tc39/test262/tree/00ef6331a62b06b02badfc7152d7f740ea31f4aa)
