        1. Assert: _intrinsicDefaultProto_ is a String value that is this specification's name of an intrinsic object. The corresponding object must be an intrinsic that is intended to be used as the [[Prototype]] value of an object.
        1. Assert: IsCallable(_constructor_) is *true*.
        1. Let _proto_ be ? Get(_constructor_, `"prototype"`).
        1. If Type(_proto_) is not Object, then
          1. Let _realm_ be ? GetFunctionRealm(_constructor_).
          1. Set _proto_ to _realm_'s intrinsic object named _intrinsicDefaultProto_.
        1. Return _proto_.