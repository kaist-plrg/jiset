            1. Let _DclRec_ be _envRec_.[[DeclarativeRecord]].
            1. If _DclRec_.HasBinding(_N_) is *true*, throw a *TypeError* exception.
            1. Return _DclRec_.CreateImmutableBinding(_N_, _S_).