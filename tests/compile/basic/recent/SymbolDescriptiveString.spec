            1. Assert: Type(_sym_) is Symbol.
            1. Let _desc_ be _sym_'s [[Description]] value.
            1. If _desc_ is *undefined*, set _desc_ to the empty String.
            1. Assert: Type(_desc_) is String.
            1. Return the string-concatenation of *"Symbol("*, _desc_, and *")"*.