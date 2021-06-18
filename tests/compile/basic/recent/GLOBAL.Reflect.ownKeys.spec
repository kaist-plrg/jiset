        1. If Type(_target_) is not Object, throw a *TypeError* exception.
        1. Let _keys_ be ? _target_.[[OwnPropertyKeys]]().
        1. Return CreateArrayFromList(_keys_).