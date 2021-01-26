            1. Let _p_ be _F_.[[RevocableProxy]].
            1. If _p_ is *null*, return *undefined*.
            1. Set _F_.[[RevocableProxy]] to *null*.
            1. Assert: _p_ is a Proxy object.
            1. Set _p_.[[ProxyTarget]] to *null*.
            1. Set _p_.[[ProxyHandler]] to *null*.
            1. Return *undefined*.