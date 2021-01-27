          1. Assert: _F_ is an ECMAScript function.
          1. Assert: Type(_newTarget_) is Undefined or Object.
          1. Let _env_ be a new Lexical Environment.
          1. Let _envRec_ be a new function Environment Record containing no bindings.
          1. Set _envRec_.[[FunctionObject]] to _F_.
          1. If _F_'s [[ThisMode]] internal slot is ~lexical~, set _envRec_.[[ThisBindingStatus]] to `"lexical"`.
          1. Else, set _envRec_.[[ThisBindingStatus]] to `"uninitialized"`.
          1. Let _home_ be the value of _F_'s [[HomeObject]] internal slot.
          1. Set _envRec_.[[HomeObject]] to _home_.
          1. Set _envRec_.[[NewTarget]] to _newTarget_.
          1. Set _env_'s EnvironmentRecord to _envRec_.
          1. Set the outer lexical environment reference of _env_ to the value of _F_'s [[Environment]] internal slot.
          1. Return _env_.