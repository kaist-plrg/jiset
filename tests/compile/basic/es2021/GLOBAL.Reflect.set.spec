        1. If Type(_target_) is not Object, throw a *TypeError* exception.
        1. Let _key_ be ? ToPropertyKey(_propertyKey_).
        1. If _receiver_ is not present, then
          1. Set _receiver_ to _target_.
        1. Return ? _target_.[[Set]](_key_, _V_, _receiver_).