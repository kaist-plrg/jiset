          1. Let _result_ be the result of evaluating |ModuleItemList|.
          1. If _result_.[[Type]] is ~normal~ and _result_.[[Value]] is ~empty~, then
            1. Return NormalCompletion(*undefined*).
          1. Return Completion(_result_).