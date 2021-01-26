          1. Let _obj_ be ? ToObject(_O_).
          1. Let _key_ be ? ToPropertyKey(_P_).
          1. Let _desc_ be ? _obj_.[[GetOwnProperty]](_key_).
          1. Return FromPropertyDescriptor(_desc_).