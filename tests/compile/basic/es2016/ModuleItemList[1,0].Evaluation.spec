          1. Let _sl_ be the result of evaluating |ModuleItemList|.
          1. ReturnIfAbrupt(_sl_).
          1. Let _s_ be the result of evaluating |ModuleItem|.
          1. Return Completion(UpdateEmpty(_s_, _sl_.[[Value]])).