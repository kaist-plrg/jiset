          1. Let _O_ be ? ToObject(*this* value).
          1. Let _sourceLen_ be ? LengthOfArrayLike(_O_).
          1. If ! IsCallable(_mapperFunction_) is *false*, throw a *TypeError* exception.
          1. Let _A_ be ? ArraySpeciesCreate(_O_, 0).
          1. Perform ? FlattenIntoArray(_A_, _O_, _sourceLen_, 0, 1, _mapperFunction_, _thisArg_).
          1. Return _A_.