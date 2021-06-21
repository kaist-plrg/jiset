          1. Let _v_ be ? GetV(_value_, _propertyName_).
          1. If |Initializer| is present and _v_ is *undefined*, then
            1. Let _defaultValue_ be the result of evaluating |Initializer|.
            1. Set _v_ to ? GetValue(_defaultValue_).
          1. Return the result of performing BindingInitialization for |BindingPattern| passing _v_ and _environment_ as arguments.