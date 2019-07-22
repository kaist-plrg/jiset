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

# Automatic Semantics Extraction

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
### Packrat parser + 1 lookahaed
- combination of recursive descent parsers with predictive parsers
- implicit lookaheads
### Converting ES-BNF into Parsers
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
