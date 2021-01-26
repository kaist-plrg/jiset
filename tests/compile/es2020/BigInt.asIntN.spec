          1. Set _bits_ to ? ToIndex(_bits_).
          1. Set _bigint_ to ? ToBigInt(_bigint_).
          1. Let _mod_ be the BigInt value that represents _bigint_ modulo 2<sup>_bits_</sup>.
          1. If _mod_ ≥ 2<sup>_bits_ - 1</sup>, return _mod_ - 2<sup>_bits_</sup>; otherwise, return _mod_.