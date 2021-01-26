          1. Let _O_ be ? ToObject(*this* value).
          1. Let _len_ be ? ToLength(? Get(_O_, `"length"`)).
          1. If _separator_ is *undefined*, let _sep_ be the single-element String `","`.
          1. Else, let _sep_ be ? ToString(_separator_).
          1. Let _R_ be the empty String.
          1. Let _k_ be 0.
          1. Repeat, while _k_ < _len_
            1. If _k_ > 0, set _R_ to the string-concatenation of _R_ and _sep_.
            1. Let _element_ be ? Get(_O_, ! ToString(_k_)).
            1. If _element_ is *undefined* or *null*, let _next_ be the empty String; otherwise, let _next_ be ? ToString(_element_).
            1. Set _R_ to the string-concatenation of _R_ and _next_.
            1. Increase _k_ by 1.
          1. Return _R_.