          1. Let _current_ be ? _O_.[[GetOwnProperty]](_P_).
          1. Let _extensible_ be ? IsExtensible(_O_).
          1. Return ValidateAndApplyPropertyDescriptor(_O_, _P_, _extensible_, _Desc_, _current_).