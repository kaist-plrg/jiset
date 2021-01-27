        1. Let _iterator_ be ObjectCreate(%IteratorPrototype%, « [[IteratedList]], [[ListIteratorNextIndex]] »).
        1. Set _iterator_.[[IteratedList]] to _list_.
        1. Set _iterator_.[[ListIteratorNextIndex]] to 0.
        1. Let _next_ be a new built-in function object as defined in ListIterator `next` (<emu-xref href="#sec-listiterator-next"></emu-xref>).
        1. Perform CreateMethodProperty(_iterator_, `"next"`, _next_).
        1. Return _iterator_.