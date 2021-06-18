          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. If _regexp_ is neither *undefined* nor *null*, then
            1. Let _searcher_ be ? GetMethod(_regexp_, @@search).
            1. If _searcher_ is not *undefined*, then
              1. Return ? Call(_searcher_, _regexp_, « _O_ »).
          1. Let _string_ be ? ToString(_O_).
          1. Let _rx_ be ? RegExpCreate(_regexp_, *undefined*).
          1. Return ? Invoke(_rx_, @@search, « _string_ »).