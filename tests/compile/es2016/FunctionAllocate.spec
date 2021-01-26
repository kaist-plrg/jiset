        1. Assert: Type(_functionPrototype_) is Object.
        1. Assert: _functionKind_ is either `"normal"`, `"non-constructor"` or `"generator"`.
        1. If _functionKind_ is `"normal"`, let _needsConstruct_ be *true*.
        1. Else, let _needsConstruct_ be *false*.
        1. If _functionKind_ is `"non-constructor"`, let _functionKind_ be `"normal"`.
        1. Let _F_ be a newly created ECMAScript function object with the internal slots listed in <emu-xref href="#table-27"></emu-xref>. All of those internal slots are initialized to *undefined*.
        1. Set _F_'s essential internal methods to the default ordinary object definitions specified in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots"></emu-xref>.
        1. Set _F_'s [[Call]] internal method to the definition specified in <emu-xref href="#sec-ecmascript-function-objects-call-thisargument-argumentslist"></emu-xref>.
        1. If _needsConstruct_ is *true*, then
          1. Set _F_'s [[Construct]] internal method to the definition specified in <emu-xref href="#sec-ecmascript-function-objects-construct-argumentslist-newtarget"></emu-xref>.
          1. Set the [[ConstructorKind]] internal slot of _F_ to `"base"`.
        1. Set the [[Strict]] internal slot of _F_ to _strict_.
        1. Set the [[FunctionKind]] internal slot of _F_ to _functionKind_.
        1. Set the [[Prototype]] internal slot of _F_ to _functionPrototype_.
        1. Set the [[Extensible]] internal slot of _F_ to *true*.
        1. Set the [[Realm]] internal slot of _F_ to the current Realm Record.
        1. Return _F_.