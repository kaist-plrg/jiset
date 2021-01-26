          1. Let _lhs_ be ? ResolveBinding(StringValue of |BindingIdentifier|, _environment_).
          1. Let _restObj_ be OrdinaryObjectCreate(%Object.prototype%).
          1. Perform ? CopyDataProperties(_restObj_, _value_, _excludedNames_).
          1. If _environment_ is *undefined*, return PutValue(_lhs_, _restObj_).
          1. Return InitializeReferencedBinding(_lhs_, _restObj_).