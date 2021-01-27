        1. Let _list_ be NonConstructorMethodDefinitions of |ClassElementList|.
        1. If |ClassElement| is the production <emu-grammar>ClassElement : `;`</emu-grammar> , return _list_.
        1. If IsStatic of |ClassElement| is *false* and PropName of |ClassElement| is `"constructor"`, return _list_.
        1. Append |ClassElement| to the end of _list_.
        1. Return _list_.