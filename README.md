# JISET
JavaScript IR-based Semantics Extraction Toolchain

## Environment Setting
```
export JISET_HOME="<<JISET directory path>>"
export PATH="$JISET_HOME/bin:$PATH"
```

## Install
```
git clone --recurse-submodules git@github.com:kaist-plrg/jiset.git
cd jiset
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
