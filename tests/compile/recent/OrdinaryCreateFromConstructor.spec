        1. Assert: _intrinsicDefaultProto_ is a String value that is this specification's name of an intrinsic object. The corresponding object must be an intrinsic that is intended to be used as the [[Prototype]] value of an object.
        1. Let _proto_ be ? GetPrototypeFromConstructor(_constructor_, _intrinsicDefaultProto_).
        1. Return ! OrdinaryObjectCreate(_proto_, _internalSlotsList_).