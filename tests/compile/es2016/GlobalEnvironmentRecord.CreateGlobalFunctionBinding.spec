            1. Let _envRec_ be the global Environment Record for which the method was invoked.
            1. Let _ObjRec_ be _envRec_.[[ObjectRecord]].
            1. Let _globalObject_ be the binding object for _ObjRec_.
            1. Let _existingProp_ be ? _globalObject_.[[GetOwnProperty]](_N_).
            1. If _existingProp_ is *undefined* or _existingProp_.[[Configurable]] is *true*, then
              1. Let _desc_ be the PropertyDescriptor{[[Value]]: _V_, [[Writable]]: *true*, [[Enumerable]]: *true*, [[Configurable]]: _D_}.
            1. Else,
              1. Let _desc_ be the PropertyDescriptor{[[Value]]: _V_ }.
            1. Perform ? DefinePropertyOrThrow(_globalObject_, _N_, _desc_).
            1. Record that the binding for _N_ in _ObjRec_ has been initialized.
            1. Perform ? Set(_globalObject_, _N_, _V_, *false*).
            1. Let _varDeclaredNames_ be _envRec_.[[VarNames]].
            1. If _varDeclaredNames_ does not contain the value of _N_, then
              1. Append _N_ to _varDeclaredNames_.
            1. Return NormalCompletion(~empty~).