          1. Let _O_ be the *this* value.
          1. Let _f_ be the active function object.
          1. If _O_ does not have a [[IteratorNext]] internal slot, throw a *TypeError* exception.
          1. Let _next_ be the value of the [[IteratorNext]] internal slot of _O_.
          1. If SameValue(_f_, _next_) is *false*, throw a *TypeError* exception.
          1. If _O_ does not have an [[IteratedList]] internal slot, throw a *TypeError* exception.
          1. Let _list_ be the value of the [[IteratedList]] internal slot of _O_.
          1. Let _index_ be the value of the [[ListIteratorNextIndex]] internal slot of _O_.
          1. Let _len_ be the number of elements of _list_.
          1. If _index_ ≥ _len_, then
            1. Return CreateIterResultObject(*undefined*, *true*).
          1. Set the value of the [[ListIteratorNextIndex]] internal slot of _O_ to _index_+1.
          1. Return CreateIterResultObject(_list_[_index_], *false*).