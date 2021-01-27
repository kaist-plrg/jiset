        1. If Type(_target_) is not Object, throw a *TypeError* exception.
        1. Let _key_ be ? ToPropertyKey(_propertyKey_).
        1. If _receiver_ is not present, then
          1. Let _receiver_ be _target_.
        1. Return ? _target_.[[Get]](_key_, _receiver_).