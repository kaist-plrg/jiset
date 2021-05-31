        1. Let _handler_ be _O_.[[ProxyHandler]].
        1. If _handler_ is *null*, throw a *TypeError* exception.
        1. Assert: Type(_handler_) is Object.
        1. Let _target_ be _O_.[[ProxyTarget]].
        1. Let _trap_ be ? GetMethod(_handler_, *"apply"*).
        1. If _trap_ is *undefined*, then
          1. Return ? Call(_target_, _thisArgument_, _argumentsList_).
        1. Let _argArray_ be ! CreateArrayFromList(_argumentsList_).
        1. Return ? Call(_trap_, _handler_, « _target_, _thisArgument_, _argArray_ »).