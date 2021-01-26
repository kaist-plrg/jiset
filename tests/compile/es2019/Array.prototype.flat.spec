          1. Let _O_ be ? ToObject(*this* value).
          1. Let _sourceLen_ be ? ToLength(? Get(_O_, `"length"`)).
          1. Let _depthNum_ be 1.
          1. If _depth_ is not *undefined*, then
            1. Set _depthNum_ to ? ToInteger(_depth_).
          1. Let _A_ be ? ArraySpeciesCreate(_O_, 0).
          1. Perform ? FlattenIntoArray(_A_, _O_, _sourceLen_, 0, _depthNum_).
          1. Return _A_.