          1. Let _len_ be _nextIndex_ + 1.
          1. Perform ? Set(_array_, *"length"*, 𝔽(_len_), *true*).
          1. NOTE: The above Set throws if _len_ exceeds 2<sup>32</sup>-1.
          1. Return _len_.