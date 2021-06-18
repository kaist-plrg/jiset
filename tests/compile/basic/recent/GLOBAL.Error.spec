          1. If NewTarget is *undefined*, let _newTarget_ be the active function object; else let _newTarget_ be NewTarget.
          1. Let _O_ be ? OrdinaryCreateFromConstructor(_newTarget_, *"%Error.prototype%"*, « [[ErrorData]] »).
          1. If _message_ is not *undefined*, then
            1. Let _msg_ be ? ToString(_message_).
            1. Let _msgDesc_ be the PropertyDescriptor { [[Value]]: _msg_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *true* }.
            1. Perform ! DefinePropertyOrThrow(_O_, *"message"*, _msgDesc_).
          1. Return _O_.