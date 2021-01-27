        1. Assert: Type(_functionPrototype_) is Object.
        1. Assert: _functionKind_ is either `"normal"`, `"non-constructor"`, `"generator"`, or `"async"`.
        1. If _functionKind_ is `"normal"`, let _needsConstruct_ be *true*.
        1. Else, let _needsConstruct_ be *false*.
        1. If _functionKind_ is `"non-constructor"`, set _functionKind_ to `"normal"`.
        1. Let _F_ be a newly created ECMAScript function object with the internal slots listed in <emu-xref href="#table-27"></emu-xref>. All of those internal slots are initialized to *undefined*.
        1. Set _F_'s essential internal methods to the default ordinary object definitions specified in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots"></emu-xref>.
        1. Set _F_.[[Call]] to the definition specified in <emu-xref href="#sec-ecmascript-function-objects-call-thisargument-argumentslist"></emu-xref>.
        1. If _needsConstruct_ is *true*, then
          1. Set _F_.[[Construct]] to the definition specified in <emu-xref href="#sec-ecmascript-function-objects-construct-argumentslist-newtarget"></emu-xref>.
          1. Set _F_.[[ConstructorKind]] to `"base"`.
        1. Set _F_.[[Strict]] to _strict_.
        1. Set _F_.[[FunctionKind]] to _functionKind_.
        1. Set _F_.[[Prototype]] to _functionPrototype_.
        1. Set _F_.[[Extensible]] to *true*.
        1. Set _F_.[[Realm]] to the current Realm Record.
        1. Return _F_.