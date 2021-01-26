          1. Assert: IsPropertyKey(_P_) is *true*.
          1. If Type(_P_) is Symbol, then
            1. Return ? OrdinaryGet(_O_, _P_, _Receiver_).
          1. Let _exports_ be _O_.[[Exports]].
          1. If _P_ is not an element of _exports_, return *undefined*.
          1. Let _m_ be _O_.[[Module]].
          1. Let _binding_ be ! _m_.ResolveExport(_P_, « »).
          1. Assert: _binding_ is a ResolvedBinding Record.
          1. Let _targetModule_ be _binding_.[[Module]].
          1. Assert: _targetModule_ is not *undefined*.
          1. Let _targetEnv_ be _targetModule_.[[Environment]].
          1. If _targetEnv_ is *undefined*, throw a *ReferenceError* exception.
          1. Let _targetEnvRec_ be _targetEnv_'s EnvironmentRecord.
          1. Return ? _targetEnvRec_.GetBindingValue(_binding_.[[BindingName]], *true*).