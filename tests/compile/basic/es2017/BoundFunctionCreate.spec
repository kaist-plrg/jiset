          1. Assert: Type(_targetFunction_) is Object.
          1. Let _proto_ be ? _targetFunction_.[[GetPrototypeOf]]().
          1. Let _obj_ be a newly created object.
          1. Set _obj_'s essential internal methods to the default ordinary object definitions specified in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots"></emu-xref>.
          1. Set _obj_.[[Call]] as described in <emu-xref href="#sec-bound-function-exotic-objects-call-thisargument-argumentslist"></emu-xref>.
          1. If _targetFunction_ has a [[Construct]] internal method, then
            1. Set _obj_.[[Construct]] as described in <emu-xref href="#sec-bound-function-exotic-objects-construct-argumentslist-newtarget"></emu-xref>.
          1. Set _obj_.[[Prototype]] to _proto_.
          1. Set _obj_.[[Extensible]] to *true*.
          1. Set _obj_.[[BoundTargetFunction]] to _targetFunction_.
          1. Set _obj_.[[BoundThis]] to _boundThis_.
          1. Set _obj_.[[BoundArguments]] to _boundArgs_.
          1. Return _obj_.