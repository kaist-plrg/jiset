          1. Let _generator_ be the *this* value.
          1. Let _completion_ be Completion { [[Type]]: ~return~, [[Value]]: _value_, [[Target]]: ~empty~ }.
          1. Return ! AsyncGeneratorEnqueue(_generator_, _completion_).