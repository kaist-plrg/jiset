        1. Let _head_ be ConstructorMethod of |ClassElementList|.
        1. If _head_ is not ~empty~, return _head_.
        1. If ClassElementKind of |ClassElement| is ~ConstructorMethod~, return |ClassElement|.
        1. Return ~empty~.