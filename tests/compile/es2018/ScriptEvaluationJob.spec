        1. Assert: _sourceText_ is an ECMAScript source text (see clause <emu-xref href="#sec-ecmascript-language-source-code"></emu-xref>).
        1. Let _realm_ be the current Realm Record.
        1. Let _s_ be ParseScript(_sourceText_, _realm_, _hostDefined_).
        1. If _s_ is a List of errors, then
          1. Perform HostReportErrors(_s_).
          1. Return NormalCompletion(*undefined*).
        1. Return ? ScriptEvaluation(_s_).