          1. Assert: Type(_O_) is Object.
          1. Assert: _hint_ is either ~string~ or ~number~.
          1. If _hint_ is ~string~, then
            1. Let _methodNames_ be « *"toString"*, *"valueOf"* ».
          1. Else,
            1. Let _methodNames_ be « *"valueOf"*, *"toString"* ».
          1. For each element _name_ of _methodNames_, do
            1. Let _method_ be ? Get(_O_, _name_).
            1. If IsCallable(_method_) is *true*, then
              1. Let _result_ be ? Call(_method_, _O_).
              1. If Type(_result_) is not Object, return _result_.
          1. Throw a *TypeError* exception.