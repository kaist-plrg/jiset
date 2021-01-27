        1. Let _stmtResult_ be the result of evaluating |SwitchStatement|.
        1. If _stmtResult_.[[Type]] is ~break~, then
          1. If _stmtResult_.[[Target]] is ~empty~, then
            1. If _stmtResult_.[[Value]] is ~empty~, let _stmtResult_ be NormalCompletion(*undefined*).
            1. Else, let _stmtResult_ be NormalCompletion(_stmtResult_.[[Value]]).
        1. Return Completion(_stmtResult_).