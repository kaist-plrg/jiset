          1. Let _obj_ be ObjectCreate(%ObjectPrototype%).
          1. Let _status_ be the result of performing PropertyDefinitionEvaluation of |PropertyDefinitionList| with arguments _obj_ and *true*.
          1. ReturnIfAbrupt(_status_).
          1. Return _obj_.