        1. If Type(_target_) is not Object, throw a *TypeError* exception.
        1. If _target_ is a Proxy exotic object and _target_.[[ProxyHandler]] is *null*, throw a *TypeError* exception.
        1. If Type(_handler_) is not Object, throw a *TypeError* exception.
        1. If _handler_ is a Proxy exotic object and _handler_.[[ProxyHandler]] is *null*, throw a *TypeError* exception.
        1. Let _P_ be ! MakeBasicObject(« [[ProxyHandler]], [[ProxyTarget]] »).
        1. Set _P_'s essential internal methods, except for [[Call]] and [[Construct]], to the definitions specified in <emu-xref href="#sec-proxy-object-internal-methods-and-internal-slots"></emu-xref>.
        1. If IsCallable(_target_) is *true*, then
          1. Set _P_.[[Call]] as specified in <emu-xref href="#sec-proxy-object-internal-methods-and-internal-slots-call-thisargument-argumentslist"></emu-xref>.
          1. If IsConstructor(_target_) is *true*, then
            1. Set _P_.[[Construct]] as specified in <emu-xref href="#sec-proxy-object-internal-methods-and-internal-slots-construct-argumentslist-newtarget"></emu-xref>.
        1. Set _P_.[[ProxyTarget]] to _target_.
        1. Set _P_.[[ProxyHandler]] to _handler_.
        1. Return _P_.