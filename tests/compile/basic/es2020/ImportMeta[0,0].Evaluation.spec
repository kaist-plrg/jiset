          1. Let _module_ be ! GetActiveScriptOrModule().
          1. Assert: _module_ is a Source Text Module Record.
          1. Let _importMeta_ be _module_.[[ImportMeta]].
          1. If _importMeta_ is ~empty~, then
            1. Set _importMeta_ to ! OrdinaryObjectCreate(*null*).
            1. Let _importMetaValues_ be ! HostGetImportMetaProperties(_module_).
            1. For each Record { [[Key]], [[Value]] } _p_ that is an element of _importMetaValues_, do
              1. Perform ! CreateDataPropertyOrThrow(_importMeta_, _p_.[[Key]], _p_.[[Value]]).
            1. Perform ! HostFinalizeImportMeta(_importMeta_, _module_).
            1. Set _module_.[[ImportMeta]] to _importMeta_.
            1. Return _importMeta_.
          1. Else,
            1. Assert: Type(_importMeta_) is Object.
            1. Return _importMeta_.