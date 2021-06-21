          1. Let _boundNames_ be ? PropertyBindingInitialization of |BindingPropertyList| with arguments _value_ and _environment_.
          1. Let _nextNames_ be ? PropertyBindingInitialization of |BindingProperty| with arguments _value_ and _environment_.
          1. Append each item in _nextNames_ to the end of _boundNames_.
          1. Return _boundNames_.