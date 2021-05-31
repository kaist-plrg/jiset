        1. Let _closure_ be a new Abstract Closure with no parameters that captures _list_ and performs the following steps when called:
          1. For each element _E_ of _list_, do
            1. Perform ? Yield(_E_).
          1. Return *undefined*.
        1. Let _iterator_ be ! CreateIteratorFromClosure(_closure_, ~empty~, %IteratorPrototype%).
        1. Return Record { [[Iterator]]: _iterator_, [[NextMethod]]: %GeneratorFunction.prototype.prototype.next%, [[Done]]: *false* }.