          1. If Type(_O_) is not Object, throw a *TypeError* exception.
          1. Let _key_ be ? ToPropertyKey(_P_).
          1. Let _desc_ be ? ToPropertyDescriptor(_Attributes_).
          1. Perform ? DefinePropertyOrThrow(_O_, _key_, _desc_).
          1. Return _O_.