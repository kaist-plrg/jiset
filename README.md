# ASE
Automatic Semantics Extraction

## Install
git clone --recurse-submodules git@github.com:kaist-plrg/ase.git
sbt generateModel

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
