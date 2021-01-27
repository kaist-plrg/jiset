            1. Let _envRec_ be the global Environment Record for which the method was invoked.
            1. Let _DclRec_ be _envRec_.[[DeclarativeRecord]].
            1. If _DclRec_.HasBinding(_N_) is *true*, then
              1. Return _DclRec_.InitializeBinding(_N_, _V_).
            1. Assert: If the binding exists, it must be in the object Environment Record.
            1. Let _ObjRec_ be _envRec_.[[ObjectRecord]].
            1. Return ? _ObjRec_.InitializeBinding(_N_, _V_).