          1. Let _R_ be the *this* value.
          1. If Type(_R_) is not Object, throw a *TypeError* exception.
          1. Let _S_ be ? ToString(_string_).
          1. Let _C_ be ? SpeciesConstructor(_R_, %RegExp%).
          1. Let _flags_ be ? ToString(? Get(_R_, *"flags"*)).
          1. Let _matcher_ be ? Construct(_C_, « _R_, _flags_ »).
          1. Let _lastIndex_ be ? ToLength(? Get(_R_, *"lastIndex"*)).
          1. Perform ? Set(_matcher_, *"lastIndex"*, _lastIndex_, *true*).
          1. If _flags_ contains *"g"*, let _global_ be *true*.
          1. Else, let _global_ be *false*.
          1. If _flags_ contains *"u"*, let _fullUnicode_ be *true*.
          1. Else, let _fullUnicode_ be *false*.
          1. Return ! CreateRegExpStringIterator(_matcher_, _S_, _global_, _fullUnicode_).