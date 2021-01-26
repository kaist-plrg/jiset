          1. Let _bindingId_ be StringValue of |BindingIdentifier|.
          1. Let _lhs_ be ResolveBinding(_bindingId_).
          1. Let _rhs_ be the result of evaluating |Initializer|.
          1. Let _value_ be ? GetValue(_rhs_).
          1. If IsAnonymousFunctionDefinition(|Initializer|) is *true*, then
            1. Let _hasNameProperty_ be ? HasOwnProperty(_value_, `"name"`).
            1. If _hasNameProperty_ is *false*, perform SetFunctionName(_value_, _bindingId_).
          1. Return InitializeReferencedBinding(_lhs_, _value_).