        1. Let _B_ be the result of evaluating |Block|.
        1. If _B_.[[Type]] is ~throw~, let _C_ be CatchClauseEvaluation of |Catch| with parameter _B_.[[Value]].
        1. Else, let _C_ be _B_.
        1. Return Completion(UpdateEmpty(_C_, *undefined*)).