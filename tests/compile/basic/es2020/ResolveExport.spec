            1. If _resolveSet_ is not present, set _resolveSet_ to a new empty List.
            1. Assert: _resolveSet_ is a List of Record { [[Module]], [[ExportName]] }.
            1. Let _module_ be this Source Text Module Record.
            1. For each Record { [[Module]], [[ExportName]] } _r_ in _resolveSet_, do
              1. If _module_ and _r_.[[Module]] are the same Module Record and SameValue(_exportName_, _r_.[[ExportName]]) is *true*, then
                1. Assert: This is a circular import request.
                1. Return *null*.
            1. Append the Record { [[Module]]: _module_, [[ExportName]]: _exportName_ } to _resolveSet_.
            1. For each ExportEntry Record _e_ in _module_.[[LocalExportEntries]], do
              1. If SameValue(_exportName_, _e_.[[ExportName]]) is *true*, then
                1. Assert: _module_ provides the direct binding for this export.
                1. Return ResolvedBinding Record { [[Module]]: _module_, [[BindingName]]: _e_.[[LocalName]] }.
            1. For each ExportEntry Record _e_ in _module_.[[IndirectExportEntries]], do
              1. If SameValue(_exportName_, _e_.[[ExportName]]) is *true*, then
                1. Let _importedModule_ be ? HostResolveImportedModule(_module_, _e_.[[ModuleRequest]]).
                1. If _e_.[[ImportName]] is *"\*"*, then
                  1. Assert: _module_ does not provide the direct binding for this export.
                  1. Return ResolvedBinding Record { [[Module]]: _importedModule_, [[BindingName]]: *"\*namespace\*"* }.
                1. Else,
                  1. Assert: _module_ imports a specific binding for this export.
                  1. Return _importedModule_.ResolveExport(_e_.[[ImportName]], _resolveSet_).
            1. If SameValue(_exportName_, *"default"*) is *true*, then
              1. Assert: A `default` export was not explicitly defined by this module.
              1. Return *null*.
              1. NOTE: A `default` export cannot be provided by an `export *` or `export * from "mod"` declaration.
            1. Let _starResolution_ be *null*.
            1. For each ExportEntry Record _e_ in _module_.[[StarExportEntries]], do
              1. Let _importedModule_ be ? HostResolveImportedModule(_module_, _e_.[[ModuleRequest]]).
              1. Let _resolution_ be ? _importedModule_.ResolveExport(_exportName_, _resolveSet_).
              1. If _resolution_ is *"ambiguous"*, return *"ambiguous"*.
              1. If _resolution_ is not *null*, then
                1. Assert: _resolution_ is a ResolvedBinding Record.
                1. If _starResolution_ is *null*, set _starResolution_ to _resolution_.
                1. Else,
                  1. Assert: There is more than one `*` import that includes the requested name.
                  1. If _resolution_.[[Module]] and _starResolution_.[[Module]] are not the same Module Record or SameValue(_resolution_.[[BindingName]], _starResolution_.[[BindingName]]) is *false*, return *"ambiguous"*.
            1. Return _starResolution_.