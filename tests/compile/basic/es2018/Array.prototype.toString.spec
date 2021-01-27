          1. Let _array_ be ? ToObject(*this* value).
          1. Let _func_ be ? Get(_array_, `"join"`).
          1. If IsCallable(_func_) is *false*, let _func_ be the intrinsic function %ObjProto_toString%.
          1. Return ? Call(_func_, _array_).