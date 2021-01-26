        1. Let _B_ be the result of evaluating |Block|.
        1. Let _F_ be the result of evaluating |Finally|.
        1. If _F_.[[Type]] is ~normal~, set _F_ to _B_.
        1. Return Completion(UpdateEmpty(_F_, *undefined*)).