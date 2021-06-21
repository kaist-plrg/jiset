        1. Let _handler_ be _O_.[[ProxyHandler]].
        1. If _handler_ is *null*, throw a *TypeError* exception.
        1. Assert: Type(_handler_) is Object.
        1. Let _target_ be _O_.[[ProxyTarget]].
        1. Let _trap_ be ? GetMethod(_handler_, *"preventExtensions"*).
        1. If _trap_ is *undefined*, then
          1. Return ? _target_.[[PreventExtensions]]().
        1. Let _booleanTrapResult_ be ! ToBoolean(? Call(_trap_, _handler_, « _target_ »)).
        1. If _booleanTrapResult_ is *true*, then
          1. Let _extensibleTarget_ be ? IsExtensible(_target_).
          1. If _extensibleTarget_ is *true*, throw a *TypeError* exception.
        1. Return _booleanTrapResult_.