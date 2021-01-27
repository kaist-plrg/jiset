        1. Let _list_ be StaticPropertyNameList of |ClassElementList|.
        1. If PropName of |ClassElement| is ~empty~, return _list_.
        1. If IsStatic of |ClassElement| is *false*, return _list_.
        1. Append PropName of |ClassElement| to the end of _list_.
        1. Return _list_.