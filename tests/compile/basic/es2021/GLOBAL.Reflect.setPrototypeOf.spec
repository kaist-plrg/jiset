        1. If Type(_target_) is not Object, throw a *TypeError* exception.
        1. If Type(_proto_) is not Object and _proto_ is not *null*, throw a *TypeError* exception.
        1. Return ? _target_.[[SetPrototypeOf]](_proto_).