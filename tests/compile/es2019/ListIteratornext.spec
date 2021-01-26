          1. Let _O_ be the *this* value.
          1. Assert: Type(_O_) is Object.
          1. Assert: _O_ has an [[IteratedList]] internal slot.
          1. Let _list_ be _O_.[[IteratedList]].
          1. Let _index_ be _O_.[[ListIteratorNextIndex]].
          1. Let _len_ be the number of elements of _list_.
          1. If _index_ ≥ _len_, then
            1. Return CreateIterResultObject(*undefined*, *true*).
          1. Set _O_.[[ListIteratorNextIndex]] to _index_ + 1.
          1. Return CreateIterResultObject(_list_[_index_], *false*).