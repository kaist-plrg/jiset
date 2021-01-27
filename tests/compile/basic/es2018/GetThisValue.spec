          1. Assert: IsPropertyReference(_V_) is *true*.
          1. If IsSuperReference(_V_) is *true*, then
            1. Return the value of the thisValue component of the reference _V_.
          1. Return GetBase(_V_).