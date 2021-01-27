            1. Let _ObjRec_ be _envRec_.[[ObjectRecord]].
            1. Let _globalObject_ be the binding object for _ObjRec_.
            1. Let _hasProperty_ be ? HasOwnProperty(_globalObject_, _N_).
            1. Let _extensible_ be ? IsExtensible(_globalObject_).
            1. If _hasProperty_ is *false* and _extensible_ is *true*, then
              1. Perform ? _ObjRec_.CreateMutableBinding(_N_, _D_).
              1. Perform ? _ObjRec_.InitializeBinding(_N_, *undefined*).
            1. Let _varDeclaredNames_ be _envRec_.[[VarNames]].
            1. If _varDeclaredNames_ does not contain _N_, then
              1. Append _N_ to _varDeclaredNames_.
            1. Return NormalCompletion(~empty~).