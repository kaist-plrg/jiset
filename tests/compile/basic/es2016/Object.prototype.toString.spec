          1. If the *this* value is *undefined*, return `"[object Undefined]"`.
          1. If the *this* value is *null*, return `"[object Null]"`.
          1. Let _O_ be ToObject(*this* value).
          1. Let _isArray_ be ? IsArray(_O_).
          1. If _isArray_ is *true*, let _builtinTag_ be `"Array"`.
          1. Else, if _O_ is an exotic String object, let _builtinTag_ be `"String"`.
          1. Else, if _O_ has an [[ParameterMap]] internal slot, let _builtinTag_ be `"Arguments"`.
          1. Else, if _O_ has a [[Call]] internal method, let _builtinTag_ be `"Function"`.
          1. Else, if _O_ has an [[ErrorData]] internal slot, let _builtinTag_ be `"Error"`.
          1. Else, if _O_ has a [[BooleanData]] internal slot, let _builtinTag_ be `"Boolean"`.
          1. Else, if _O_ has a [[NumberData]] internal slot, let _builtinTag_ be `"Number"`.
          1. Else, if _O_ has a [[DateValue]] internal slot, let _builtinTag_ be `"Date"`.
          1. Else, if _O_ has a [[RegExpMatcher]] internal slot, let _builtinTag_ be `"RegExp"`.
          1. Else, let _builtinTag_ be `"Object"`.
          1. Let _tag_ be ? Get(_O_, @@toStringTag).
          1. If Type(_tag_) is not String, let _tag_ be _builtinTag_.
          1. Return the String that is the result of concatenating `"[object "`, _tag_, and `"]"`.