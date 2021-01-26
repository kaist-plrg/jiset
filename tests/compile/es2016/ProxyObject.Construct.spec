        1. Let _handler_ be the value of the [[ProxyHandler]] internal slot of _O_.
        1. If _handler_ is *null*, throw a *TypeError* exception.
        1. Assert: Type(_handler_) is Object.
        1. Let _target_ be the value of the [[ProxyTarget]] internal slot of _O_.
        1. Let _trap_ be ? GetMethod(_handler_, `"construct"`).
        1. If _trap_ is *undefined*, then
          1. Assert: _target_ has a [[Construct]] internal method.
          1. Return ? Construct(_target_, _argumentsList_, _newTarget_).
        1. Let _argArray_ be CreateArrayFromList(_argumentsList_).
        1. Let _newObj_ be ? Call(_trap_, _handler_, « _target_, _argArray_, _newTarget_ »).
        1. If Type(_newObj_) is not Object, throw a *TypeError* exception.
        1. Return _newObj_.