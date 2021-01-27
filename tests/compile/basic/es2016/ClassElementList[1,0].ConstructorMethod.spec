        1. Let _head_ be ConstructorMethod of |ClassElementList|.
        1. If _head_ is not ~empty~, return _head_.
        1. If |ClassElement| is the production <emu-grammar>ClassElement : `;`</emu-grammar> , return ~empty~.
        1. If IsStatic of |ClassElement| is *true*, return ~empty~.
        1. If PropName of |ClassElement| is not `"constructor"`, return ~empty~.
        1. Return |ClassElement|.