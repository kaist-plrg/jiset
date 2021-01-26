        1. Assert: _sourceText_ is an ECMAScript source text (see clause <emu-xref href="#sec-ecmascript-language-source-code"></emu-xref>).
        1. Parse _sourceText_ using |Script| as the goal symbol and analyse the parse result for any Early Error conditions. If the parse was successful and no early errors were found, let _body_ be the resulting parse tree. Otherwise, let _body_ be a List of one or more *SyntaxError* objects representing the parsing errors and/or early errors. Parsing and early error detection may be interweaved in an implementation-dependent manner. If more than one parsing error or early error is present, the number and ordering of error objects in the list is implementation-dependent, but at least one must be present.
        1. If _body_ is a List of errors, return _body_.
        1. Return Script Record { [[Realm]]: _realm_, [[Environment]]: *undefined*, [[ECMAScriptCode]]: _body_, [[HostDefined]]: _hostDefined_ }.