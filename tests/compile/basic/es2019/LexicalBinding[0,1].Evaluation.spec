          1. Let _bindingId_ be StringValue of |BindingIdentifier|.
          1. Let _lhs_ be ResolveBinding(_bindingId_).
          1. If IsAnonymousFunctionDefinition(|Initializer|) is *true*, then
            1. Let _value_ be the result of performing NamedEvaluation for |Initializer| with argument _bindingId_.
          1. Else,
            1. Let _rhs_ be the result of evaluating |Initializer|.
            1. Let _value_ be ? GetValue(_rhs_).
          1. Return InitializeReferencedBinding(_lhs_, _value_).