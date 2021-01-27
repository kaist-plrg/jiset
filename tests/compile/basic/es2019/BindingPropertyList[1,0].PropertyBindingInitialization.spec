          1. Let _boundNames_ be the result of performing ? PropertyBindingInitialization for |BindingPropertyList| using _value_ and _environment_ as arguments.
          1. Let _nextNames_ be the result of performing ? PropertyBindingInitialization for |BindingProperty| using _value_ and _environment_ as arguments.
          1. Append each item in _nextNames_ to the end of _boundNames_.
          1. Return _boundNames_.