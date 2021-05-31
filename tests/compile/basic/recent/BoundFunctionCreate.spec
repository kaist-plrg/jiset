          1. Assert: Type(_targetFunction_) is Object.
          1. Let _proto_ be ? _targetFunction_.[[GetPrototypeOf]]().
          1. Let _internalSlotsList_ be the internal slots listed in <emu-xref href="#table-internal-slots-of-bound-function-exotic-objects"></emu-xref>, plus [[Prototype]] and [[Extensible]].
          1. Let _obj_ be ! MakeBasicObject(_internalSlotsList_).
          1. Set _obj_.[[Prototype]] to _proto_.
          1. Set _obj_.[[Call]] as described in <emu-xref href="#sec-bound-function-exotic-objects-call-thisargument-argumentslist"></emu-xref>.
          1. If IsConstructor(_targetFunction_) is *true*, then
            1. Set _obj_.[[Construct]] as described in <emu-xref href="#sec-bound-function-exotic-objects-construct-argumentslist-newtarget"></emu-xref>.
          1. Set _obj_.[[BoundTargetFunction]] to _targetFunction_.
          1. Set _obj_.[[BoundThis]] to _boundThis_.
          1. Set _obj_.[[BoundArguments]] to _boundArgs_.
          1. Return _obj_.