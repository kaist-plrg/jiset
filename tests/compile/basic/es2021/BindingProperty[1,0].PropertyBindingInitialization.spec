          1. Let _P_ be the result of evaluating |PropertyName|.
          1. ReturnIfAbrupt(_P_).
          1. Perform ? KeyedBindingInitialization of |BindingElement| with _value_, _environment_, and _P_ as the arguments.
          1. Return a List whose sole element is _P_.