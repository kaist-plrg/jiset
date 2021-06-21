          1. Let _rhs_ be the result of evaluating |Initializer|.
          1. Let _rval_ be ? GetValue(_rhs_).
          1. Return the result of performing BindingInitialization for |BindingPattern| passing _rval_ and *undefined* as arguments.