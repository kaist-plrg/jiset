            1. Let _module_ be this Source Text Module Record.
            1. If _exportStarSet_ contains _module_, then
              1. Assert: We've reached the starting point of an `import *` circularity.
              1. Return a new empty List.
            1. Append _module_ to _exportStarSet_.
            1. Let _exportedNames_ be a new empty List.
            1. For each ExportEntry Record _e_ in _module_.[[LocalExportEntries]], do
              1. Assert: _module_ provides the direct binding for this export.
              1. Append _e_.[[ExportName]] to _exportedNames_.
            1. For each ExportEntry Record _e_ in _module_.[[IndirectExportEntries]], do
              1. Assert: _module_ imports a specific binding for this export.
              1. Append _e_.[[ExportName]] to _exportedNames_.
            1. For each ExportEntry Record _e_ in _module_.[[StarExportEntries]], do
              1. Let _requestedModule_ be ? HostResolveImportedModule(_module_, _e_.[[ModuleRequest]]).
              1. Let _starNames_ be ? _requestedModule_.GetExportedNames(_exportStarSet_).
              1. For each element _n_ of _starNames_, do
                1. If SameValue(_n_, `"default"`) is *false*, then
                  1. If _n_ is not an element of _exportedNames_, then
                    1. Append _n_ to _exportedNames_.
            1. Return _exportedNames_.