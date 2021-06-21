            1. Let _DclRec_ be _envRec_.[[DeclarativeRecord]].
            1. If _DclRec_.HasBinding(_N_) is *true*, then
              1. Return _DclRec_.DeleteBinding(_N_).
            1. Let _ObjRec_ be _envRec_.[[ObjectRecord]].
            1. Let _globalObject_ be the binding object for _ObjRec_.
            1. Let _existingProp_ be ? HasOwnProperty(_globalObject_, _N_).
            1. If _existingProp_ is *true*, then
              1. Let _status_ be ? _ObjRec_.DeleteBinding(_N_).
              1. If _status_ is *true*, then
                1. Let _varNames_ be _envRec_.[[VarNames]].
                1. If _N_ is an element of _varNames_, remove that element from the _varNames_.
              1. Return _status_.
            1. Return *true*.