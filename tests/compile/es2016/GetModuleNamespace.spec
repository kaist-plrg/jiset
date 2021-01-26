          1. Assert: _module_ is an instance of a concrete subclass of Module Record.
          1. Let _namespace_ be _module_.[[Namespace]].
          1. If _namespace_ is *undefined*, then
            1. Let _exportedNames_ be ? _module_.GetExportedNames(« »).
            1. Let _unambiguousNames_ be a new empty List.
            1. For each _name_ that is an element of _exportedNames_,
              1. Let _resolution_ be ? _module_.ResolveExport(_name_, « », « »).
              1. If _resolution_ is *null*, throw a *SyntaxError* exception.
              1. If _resolution_ is not `"ambiguous"`, append _name_ to _unambiguousNames_.
            1. Let _namespace_ be ModuleNamespaceCreate(_module_, _unambiguousNames_).
          1. Return _namespace_.