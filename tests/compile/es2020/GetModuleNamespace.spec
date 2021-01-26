          1. Assert: _module_ is an instance of a concrete subclass of Module Record.
          1. Assert: If _module_ is a Cyclic Module Record, then _module_.[[Status]] is not ~unlinked~.
          1. Let _namespace_ be _module_.[[Namespace]].
          1. If _namespace_ is *undefined*, then
            1. Let _exportedNames_ be ? _module_.GetExportedNames().
            1. Let _unambiguousNames_ be a new empty List.
            1. For each _name_ that is an element of _exportedNames_, do
              1. Let _resolution_ be ? _module_.ResolveExport(_name_).
              1. If _resolution_ is a ResolvedBinding Record, append _name_ to _unambiguousNames_.
            1. Set _namespace_ to ModuleNamespaceCreate(_module_, _unambiguousNames_).
          1. Return _namespace_.