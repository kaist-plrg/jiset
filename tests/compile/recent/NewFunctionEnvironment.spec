          1. Assert: _F_ is an ECMAScript function.
          1. Assert: Type(_newTarget_) is Undefined or Object.
          1. Let _env_ be a new function Environment Record containing no bindings.
          1. Set _env_.[[FunctionObject]] to _F_.
          1. If _F_.[[ThisMode]] is ~lexical~, set _env_.[[ThisBindingStatus]] to ~lexical~.
          1. Else, set _env_.[[ThisBindingStatus]] to ~uninitialized~.
          1. Set _env_.[[NewTarget]] to _newTarget_.
          1. Set _env_.[[OuterEnv]] to _F_.[[Environment]].
          1. Return _env_.