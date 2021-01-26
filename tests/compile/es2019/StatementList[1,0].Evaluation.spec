        1. Let _sl_ be the result of evaluating |StatementList|.
        1. ReturnIfAbrupt(_sl_).
        1. Let _s_ be the result of evaluating |StatementListItem|.
        1. Return Completion(UpdateEmpty(_s_, _sl_)).