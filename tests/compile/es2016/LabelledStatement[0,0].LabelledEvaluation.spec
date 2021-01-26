        1. Let _label_ be the StringValue of |LabelIdentifier|.
        1. Append _label_ as an element of _labelSet_.
        1. Let _stmtResult_ be LabelledEvaluation of |LabelledItem| with argument _labelSet_.
        1. If _stmtResult_.[[Type]] is ~break~ and SameValue(_stmtResult_.[[Target]], _label_) is *true*, then
          1. Let _stmtResult_ be NormalCompletion(_stmtResult_.[[Value]]).
        1. Return Completion(_stmtResult_).