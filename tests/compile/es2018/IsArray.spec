        1. If Type(_argument_) is not Object, return *false*.
        1. If _argument_ is an Array exotic object, return *true*.
        1. If _argument_ is a Proxy exotic object, then
          1. If _argument_.[[ProxyHandler]] is *null*, throw a *TypeError* exception.
          1. Let _target_ be _argument_.[[ProxyTarget]].
          1. Return ? IsArray(_target_).
        1. Return *false*.