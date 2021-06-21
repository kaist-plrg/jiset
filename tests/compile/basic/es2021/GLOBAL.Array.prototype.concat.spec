          1. Let _O_ be ? ToObject(*this* value).
          1. Let _A_ be ? ArraySpeciesCreate(_O_, 0).
          1. Let _n_ be 0.
          1. Prepend _O_ to _items_.
          1. For each element _E_ of _items_, do
            1. Let _spreadable_ be ? IsConcatSpreadable(_E_).
            1. If _spreadable_ is *true*, then
              1. Let _k_ be 0.
              1. Let _len_ be ? LengthOfArrayLike(_E_).
              1. If _n_ + _len_ > 2<sup>53</sup> - 1, throw a *TypeError* exception.
              1. Repeat, while _k_ < _len_,
                1. Let _P_ be ! ToString(𝔽(_k_)).
                1. Let _exists_ be ? HasProperty(_E_, _P_).
                1. If _exists_ is *true*, then
                  1. Let _subElement_ be ? Get(_E_, _P_).
                  1. Perform ? CreateDataPropertyOrThrow(_A_, ! ToString(𝔽(_n_)), _subElement_).
                1. Set _n_ to _n_ + 1.
                1. Set _k_ to _k_ + 1.
            1. Else,
              1. NOTE: _E_ is added as a single item rather than spread.
              1. If _n_ ≥ 2<sup>53</sup> - 1, throw a *TypeError* exception.
              1. Perform ? CreateDataPropertyOrThrow(_A_, ! ToString(𝔽(_n_)), _E_).
              1. Set _n_ to _n_ + 1.
          1. [id="step-array-proto-concat-set-length"] Perform ? Set(_A_, *"length"*, 𝔽(_n_), *true*).
          1. Return _A_.