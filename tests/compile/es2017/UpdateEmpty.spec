          1. Assert: If _completionRecord_.[[Type]] is either ~return~ or ~throw~, then _completionRecord_.[[Value]] is not ~empty~.
          1. If _completionRecord_.[[Value]] is not ~empty~, return Completion(_completionRecord_).
          1. Return Completion{[[Type]]: _completionRecord_.[[Type]], [[Value]]: _value_, [[Target]]: _completionRecord_.[[Target]] }.