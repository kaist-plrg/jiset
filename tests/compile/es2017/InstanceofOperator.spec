        1. If Type(_C_) is not Object, throw a *TypeError* exception.
        1. Let _instOfHandler_ be ? GetMethod(_C_, @@hasInstance).
        1. If _instOfHandler_ is not *undefined*, then
          1. Return ToBoolean(? Call(_instOfHandler_, _C_, « _O_ »)).
        1. If IsCallable(_C_) is *false*, throw a *TypeError* exception.
        1. Return ? OrdinaryHasInstance(_C_, _O_).