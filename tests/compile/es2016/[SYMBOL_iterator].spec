        1. Let _N_ be the *this* value.
        1. If _N_ is not a module namespace exotic object, throw a *TypeError* exception.
        1. Let _exports_ be the value of _N_'s [[Exports]] internal slot.
        1. Return ! CreateListIterator(_exports_).