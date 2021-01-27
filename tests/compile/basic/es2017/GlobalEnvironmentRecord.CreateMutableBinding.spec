            1. Let _envRec_ be the global Environment Record for which the method was invoked.
            1. Let _DclRec_ be _envRec_.[[DeclarativeRecord]].
            1. If _DclRec_.HasBinding(_N_) is *true*, throw a *TypeError* exception.
            1. Return _DclRec_.CreateMutableBinding(_N_, _D_).