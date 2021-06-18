          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. If _regexp_ is neither *undefined* nor *null*, then
            1. Let _matcher_ be ? GetMethod(_regexp_, @@match).
            1. If _matcher_ is not *undefined*, then
              1. Return ? Call(_matcher_, _regexp_, « _O_ »).
          1. Let _S_ be ? ToString(_O_).
          1. Let _rx_ be ? RegExpCreate(_regexp_, *undefined*).
          1. Return ? Invoke(_rx_, @@match, « _S_ »).