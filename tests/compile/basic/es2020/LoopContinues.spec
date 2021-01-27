          1. If _completion_.[[Type]] is ~normal~, return *true*.
          1. If _completion_.[[Type]] is not ~continue~, return *false*.
          1. If _completion_.[[Target]] is ~empty~, return *true*.
          1. If _completion_.[[Target]] is an element of _labelSet_, return *true*.
          1. Return *false*.