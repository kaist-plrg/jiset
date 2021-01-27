          1. Let _O_ be ? ToObject(*this* value).
          1. Let _sourceLen_ be ? ToLength(? Get(_O_, `"length"`)).
          1. If IsCallable(_mapperFunction_) is *false*, throw a *TypeError* exception.
          1. If _thisArg_ is present, let _T_ be _thisArg_; else let _T_ be *undefined*.
          1. Let _A_ be ? ArraySpeciesCreate(_O_, 0).
          1. Perform ? FlattenIntoArray(_A_, _O_, _sourceLen_, 0, 1, _mapperFunction_, _T_).
          1. Return _A_.