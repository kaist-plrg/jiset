        1. Let _stmtResult_ be LoopEvaluation of |IterationStatement| with argument _labelSet_.
        1. If _stmtResult_.[[Type]] is ~break~, then
          1. If _stmtResult_.[[Target]] is ~empty~, then
            1. If _stmtResult_.[[Value]] is ~empty~, set _stmtResult_ to NormalCompletion(*undefined*).
            1. Else, set _stmtResult_ to NormalCompletion(_stmtResult_.[[Value]]).
        1. Return Completion(_stmtResult_).