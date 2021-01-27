          1. Let _bindingId_ be StringValue of |BindingIdentifier|.
          1. Let _lhs_ be ? ResolveBinding(_bindingId_, _environment_).
          1. Let _v_ be ? GetV(_value_, _propertyName_).
          1. If |Initializer| is present and _v_ is *undefined*, then
            1. Let _defaultValue_ be the result of evaluating |Initializer|.
            1. Let _v_ be ? GetValue(_defaultValue_).
            1. If IsAnonymousFunctionDefinition(|Initializer|) is *true*, then
              1. Let _hasNameProperty_ be ? HasOwnProperty(_v_, `"name"`).
              1. If _hasNameProperty_ is *false*, perform SetFunctionName(_v_, _bindingId_).
          1. If _environment_ is *undefined*, return ? PutValue(_lhs_, _v_).
          1. Return InitializeReferencedBinding(_lhs_, _v_).