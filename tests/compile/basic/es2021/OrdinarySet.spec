          1. Assert: IsPropertyKey(_P_) is *true*.
          1. Let _ownDesc_ be ? _O_.[[GetOwnProperty]](_P_).
          1. Return OrdinarySetWithOwnDescriptor(_O_, _P_, _V_, _Receiver_, _ownDesc_).