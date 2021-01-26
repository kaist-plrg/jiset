        1. If Type(_target_) is not Object, throw a *TypeError* exception.
        1. Let _key_ be ? ToPropertyKey(_propertyKey_).
        1. Return ? _target_.[[HasProperty]](_key_).