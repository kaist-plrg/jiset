          1. Let _P_ be StringValue of |IdentifierReference|.
          1. Let _lref_ be ? ResolveBinding(_P_).
          1. Let _v_ be ? GetV(_value_, _P_).
          1. If |Initializer_opt| is present and _v_ is *undefined*, then
            1. Let _defaultValue_ be the result of evaluating |Initializer|.
            1. Set _v_ to ? GetValue(_defaultValue_).
            1. If IsAnonymousFunctionDefinition(|Initializer|) is *true*, then
              1. Let _hasNameProperty_ be ? HasOwnProperty(_v_, `"name"`).
              1. If _hasNameProperty_ is *false*, perform SetFunctionName(_v_, _P_).
          1. Return ? PutValue(_lref_, _v_).