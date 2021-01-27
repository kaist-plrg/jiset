        1. If |ClassElement| is the production <emu-grammar>ClassElement : `;`</emu-grammar> , return a new empty List.
        1. If IsStatic of |ClassElement| is *false* and PropName of |ClassElement| is `"constructor"`, return a new empty List.
        1. Return a List containing |ClassElement|.