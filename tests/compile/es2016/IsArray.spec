        1. If Type(_argument_) is not Object, return *false*.
        1. If _argument_ is an Array exotic object, return *true*.
        1. If _argument_ is a Proxy exotic object, then
          1. If the value of the [[ProxyHandler]] internal slot of _argument_ is *null*, throw a *TypeError* exception.
          1. Let _target_ be the value of the [[ProxyTarget]] internal slot of _argument_.
          1. Return ? IsArray(_target_).
        1. Return *false*.