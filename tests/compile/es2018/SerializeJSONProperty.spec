          1. Let _value_ be ? Get(_holder_, _key_).
          1. If Type(_value_) is Object, then
            1. Let _toJSON_ be ? Get(_value_, `"toJSON"`).
            1. If IsCallable(_toJSON_) is *true*, then
              1. Set _value_ to ? Call(_toJSON_, _value_, « _key_ »).
          1. If _ReplacerFunction_ is not *undefined*, then
            1. Set _value_ to ? Call(_ReplacerFunction_, _holder_, « _key_, _value_ »).
          1. If Type(_value_) is Object, then
            1. If _value_ has a [[NumberData]] internal slot, then
              1. Set _value_ to ? ToNumber(_value_).
            1. Else if _value_ has a [[StringData]] internal slot, then
              1. Set _value_ to ? ToString(_value_).
            1. Else if _value_ has a [[BooleanData]] internal slot, then
              1. Set _value_ to _value_.[[BooleanData]].
          1. If _value_ is *null*, return `"null"`.
          1. If _value_ is *true*, return `"true"`.
          1. If _value_ is *false*, return `"false"`.
          1. If Type(_value_) is String, return QuoteJSONString(_value_).
          1. If Type(_value_) is Number, then
            1. If _value_ is finite, return ! ToString(_value_).
            1. Return `"null"`.
          1. If Type(_value_) is Object and IsCallable(_value_) is *false*, then
            1. Let _isArray_ be ? IsArray(_value_).
            1. If _isArray_ is *true*, return ? SerializeJSONArray(_value_).
            1. Return ? SerializeJSONObject(_value_).
          1. Return *undefined*.