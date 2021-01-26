            1. Let _module_ be this Cyclic Module Record.
            1. Assert: _module_.[[Status]] is not ~linking~ or ~evaluating~.
            1. Let _stack_ be a new empty List.
            1. Let _result_ be InnerModuleLinking(_module_, _stack_, 0).
            1. If _result_ is an abrupt completion, then
              1. For each Cyclic Module Record _m_ in _stack_, do
                1. Assert: _m_.[[Status]] is ~linking~.
                1. Set _m_.[[Status]] to ~unlinked~.
                1. Set _m_.[[Environment]] to *undefined*.
                1. Set _m_.[[DFSIndex]] to *undefined*.
                1. Set _m_.[[DFSAncestorIndex]] to *undefined*.
              1. Assert: _module_.[[Status]] is ~unlinked~.
              1. Return _result_.
            1. Assert: _module_.[[Status]] is ~linked~ or ~evaluated~.
            1. Assert: _stack_ is empty.
            1. Return *undefined*.