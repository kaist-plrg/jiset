          1. Let _exports_ be a copy of the value of _O_'s [[Exports]] internal slot.
          1. Let _symbolKeys_ be ! OrdinaryOwnPropertyKeys(_O_).
          1. Append all the entries of _symbolKeys_ to the end of _exports_.
          1. Return _exports_.