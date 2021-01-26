        1. Let _iterator_ be ObjectCreate(%IteratorPrototype%, « [[IteratedList]], [[ListIteratorNextIndex]] »).
        1. Set _iterator_.[[IteratedList]] to _list_.
        1. Set _iterator_.[[ListIteratorNextIndex]] to 0.
        1. Let _steps_ be the algorithm steps defined in ListIterator `next` (<emu-xref href="#sec-listiterator-next"></emu-xref>).
        1. Let _next_ be CreateBuiltinFunction(_steps_, « »).
        1. Return Record { [[Iterator]]: _iterator_, [[NextMethod]]: _next_, [[Done]]: *false* }.