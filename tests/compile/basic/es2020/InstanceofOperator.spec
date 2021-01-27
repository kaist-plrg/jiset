        1. If Type(_target_) is not Object, throw a *TypeError* exception.
        1. Let _instOfHandler_ be ? GetMethod(_target_, @@hasInstance).
        1. If _instOfHandler_ is not *undefined*, then
          1. Return ! ToBoolean(? Call(_instOfHandler_, _target_, « _V_ »)).
        1. If IsCallable(_target_) is *false*, throw a *TypeError* exception.
        1. Return ? OrdinaryHasInstance(_target_, _V_).