        1. If Type(_target_) is not Object, throw a *TypeError* exception.
        1. Let _key_ be ? ToPropertyKey(_propertyKey_).
        1. Let _desc_ be ? _target_.[[GetOwnProperty]](_key_).
        1. Return FromPropertyDescriptor(_desc_).