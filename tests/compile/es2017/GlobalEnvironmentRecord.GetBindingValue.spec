            1. Let _envRec_ be the global Environment Record for which the method was invoked.
            1. Let _DclRec_ be _envRec_.[[DeclarativeRecord]].
            1. If _DclRec_.HasBinding(_N_) is *true*, then
              1. Return _DclRec_.GetBindingValue(_N_, _S_).
            1. Let _ObjRec_ be _envRec_.[[ObjectRecord]].
            1. Return ? _ObjRec_.GetBindingValue(_N_, _S_).