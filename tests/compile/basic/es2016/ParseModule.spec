            1. Assert: _sourceText_ is an ECMAScript source text (see clause <emu-xref href="#sec-ecmascript-language-source-code"></emu-xref>).
            1. Parse _sourceText_ using |Module| as the goal symbol and analyze the parse result for any Early Error conditions. If the parse was successful and no early errors were found, let _body_ be the resulting parse tree. Otherwise, let _body_ be a List of one or more *SyntaxError* or *ReferenceError* objects representing the parsing errors and/or early errors. Parsing and early error detection may be interweaved in an implementation dependent manner. If more than one parsing error or early error is present, the number and ordering of error objects in the list is implementation dependent, but at least one must be present.
            1. If _body_ is a List of errors, then return _body_.
            1. Let _requestedModules_ be the ModuleRequests of _body_.
            1. Let _importEntries_ be ImportEntries of _body_.
            1. Let _importedBoundNames_ be ImportedLocalNames(_importEntries_).
            1. Let _indirectExportEntries_ be a new empty List.
            1. Let _localExportEntries_ be a new empty List.
            1. Let _starExportEntries_ be a new empty List.
            1. Let _exportEntries_ be ExportEntries of _body_.
            1. For each record _ee_ in _exportEntries_, do
              1. If _ee_.[[ModuleRequest]] is *null*, then
                1. If _ee_.[[LocalName]] is not an element of _importedBoundNames_, then
                  1. Append _ee_ to _localExportEntries_.
                1. Else,
                  1. Let _ie_ be the element of _importEntries_ whose [[LocalName]] is the same as _ee_.[[LocalName]].
                  1. If _ie_.[[ImportName]] is `"*"`, then
                    1. Assert: this is a re-export of an imported module namespace object.
                    1. Append _ee_ to _localExportEntries_.
                  1. Else, this is a re-export of a single name
                    1. Append to _indirectExportEntries_ the Record {[[ModuleRequest]]: _ie_.[[ModuleRequest]], [[ImportName]]: _ie_.[[ImportName]], [[LocalName]]: *null*, [[ExportName]]: _ee_.[[ExportName]] }.
              1. Else, if _ee_.[[ImportName]] is `"*"`, then
                1. Append _ee_ to _starExportEntries_.
              1. Else,
                1. Append _ee_ to _indirectExportEntries_.
            1. Return Source Text Module Record {[[Realm]]: _realm_, [[Environment]]: *undefined*, [[HostDefined]]: _hostDefined_, [[Namespace]]: *undefined*, [[Evaluated]]: *false*, [[ECMAScriptCode]]: _body_, [[RequestedModules]]: _requestedModules_, [[ImportEntries]]: _importEntries_, [[LocalExportEntries]]: _localExportEntries_, [[StarExportEntries]]: _starExportEntries_, [[IndirectExportEntries]]: _indirectExportEntries_}.