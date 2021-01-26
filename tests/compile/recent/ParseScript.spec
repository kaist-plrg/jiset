        1. Assert: _sourceText_ is an ECMAScript source text (see clause <emu-xref href="#sec-ecmascript-language-source-code"></emu-xref>).
        1. Let _body_ be ParseText(_sourceText_, |Script|).
        1. If _body_ is a List of errors, return _body_.
        1. Return Script Record { [[Realm]]: _realm_, [[Environment]]: *undefined*, [[ECMAScriptCode]]: _body_, [[HostDefined]]: _hostDefined_ }.