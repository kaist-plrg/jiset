          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. If _regexp_ is neither *undefined* nor *null*, then
            1. Let _isRegExp_ be ? IsRegExp(_regexp_).
            1. If _isRegExp_ is *true*, then
              1. Let _flags_ be ? Get(_regexp_, *"flags"*).
              1. Perform ? RequireObjectCoercible(_flags_).
              1. If ? ToString(_flags_) does not contain *"g"*, throw a *TypeError* exception.
            1. Let _matcher_ be ? GetMethod(_regexp_, @@matchAll).
            1. If _matcher_ is not *undefined*, then
              1. Return ? Call(_matcher_, _regexp_, « _O_ »).
          1. Let _S_ be ? ToString(_O_).
          1. Let _rx_ be ? RegExpCreate(_regexp_, *"g"*).
          1. Return ? Invoke(_rx_, @@matchAll, « _S_ »).