          1. Let _bindingId_ be StringValue of |BindingIdentifier|.
          1. Let _lhs_ be ? ResolveBinding(_bindingId_).
          1. If IsAnonymousFunctionDefinition(|Initializer|) is *true*, then
            1. Let _value_ be NamedEvaluation of |Initializer| with argument _bindingId_.
          1. Else,
            1. Let _rhs_ be the result of evaluating |Initializer|.
            1. Let _value_ be ? GetValue(_rhs_).
          1. [id="step-vardecllist-evaluation-putvalue"] Return ? PutValue(_lhs_, _value_).