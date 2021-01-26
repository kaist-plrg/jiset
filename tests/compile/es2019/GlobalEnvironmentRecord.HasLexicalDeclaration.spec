            1. Let _envRec_ be the global Environment Record for which the method was invoked.
            1. Let _DclRec_ be _envRec_.[[DeclarativeRecord]].
            1. Return _DclRec_.HasBinding(_N_).