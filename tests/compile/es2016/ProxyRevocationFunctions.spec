            1. Let _p_ be the value of _F_'s [[RevocableProxy]] internal slot.
            1. If _p_ is *null*, return *undefined*.
            1. Set the value of _F_'s [[RevocableProxy]] internal slot to *null*.
            1. Assert: _p_ is a Proxy object.
            1. Set the [[ProxyTarget]] internal slot of _p_ to *null*.
            1. Set the [[ProxyHandler]] internal slot of _p_ to *null*.
            1. Return *undefined*.