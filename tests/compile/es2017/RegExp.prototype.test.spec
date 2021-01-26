          1. Let _R_ be the *this* value.
          1. If Type(_R_) is not Object, throw a *TypeError* exception.
          1. Let _string_ be ? ToString(_S_).
          1. Let _match_ be ? RegExpExec(_R_, _string_).
          1. If _match_ is not *null*, return *true*; else return *false*.