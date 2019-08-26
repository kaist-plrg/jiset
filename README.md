# JISET
JavaScript IR-based Semantics Extraction Toolchain

## Install
```
git clone --recurse-submodules git@github.com:kaist-plrg/jiset.git
sbt generateModel
```

## Unsupported Features for Test262
- Parts of built-in objects
  - Date
  - Math
  - JSON
  - RegExp
- module
- strict mode
- [[CanBlock]]
- locale
