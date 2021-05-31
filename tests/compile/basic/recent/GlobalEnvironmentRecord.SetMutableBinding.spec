            1. Let _DclRec_ be _envRec_.[[DeclarativeRecord]].
            1. If _DclRec_.HasBinding(_N_) is *true*, then
              1. Return _DclRec_.SetMutableBinding(_N_, _V_, _S_).
            1. Let _ObjRec_ be _envRec_.[[ObjectRecord]].
            1. Return ? _ObjRec_.SetMutableBinding(_N_, _V_, _S_).