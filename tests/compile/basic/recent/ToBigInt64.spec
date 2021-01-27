        1. Let _n_ be ? ToBigInt(_argument_).
        1. Let _int64bit_ be ℝ(_n_) modulo 2<sup>64</sup>.
        1. If _int64bit_ ≥ 2<sup>63</sup>, return ℤ(_int64bit_ - 2<sup>64</sup>); otherwise return ℤ(_int64bit_).