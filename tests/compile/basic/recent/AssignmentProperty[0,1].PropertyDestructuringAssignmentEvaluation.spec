          1. Let _P_ be StringValue of |IdentifierReference|.
          1. Let _lref_ be ? ResolveBinding(_P_).
          1. Let _v_ be ? GetV(_value_, _P_).
          1. If |Initializer_opt| is present and _v_ is *undefined*, then
            1. If IsAnonymousFunctionDefinition(|Initializer|) is *true*, then
              1. Set _v_ to the result of performing NamedEvaluation for |Initializer| with argument _P_.
            1. Else,
              1. Let _defaultValue_ be the result of evaluating |Initializer|.
              1. Set _v_ to ? GetValue(_defaultValue_).
          1. Perform ? PutValue(_lref_, _v_).
          1. Return a List whose sole element is _P_.