          1. Let _a_ be ℝ(? ToUint32(_x_)).
          1. Let _b_ be ℝ(? ToUint32(_y_)).
          1. Let _product_ be (_a_ × _b_) modulo 2<sup>32</sup>.
          1. If _product_ ≥ 2<sup>31</sup>, return 𝔽(_product_ - 2<sup>32</sup>); otherwise return 𝔽(_product_).