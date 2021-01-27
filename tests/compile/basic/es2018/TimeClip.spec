          1. If _time_ is not finite, return *NaN*.
          1. If abs(_time_) > 8.64 × 10<sup>15</sup>, return *NaN*.
          1. Let _clippedTime_ be ! ToInteger(_time_).
          1. If _clippedTime_ is *-0*, set _clippedTime_ to *+0*.
          1. Return _clippedTime_.