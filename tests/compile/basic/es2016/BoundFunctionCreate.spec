          1. Assert: Type(_targetFunction_) is Object.
          1. Let _proto_ be ? _targetFunction_.[[GetPrototypeOf]]().
          1. Let _obj_ be a newly created object.
          1. Set _obj_'s essential internal methods to the default ordinary object definitions specified in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots"></emu-xref>.
          1. Set the [[Call]] internal method of _obj_ as described in <emu-xref href="#sec-bound-function-exotic-objects-call-thisargument-argumentslist"></emu-xref>.
          1. If _targetFunction_ has a [[Construct]] internal method, then
            1. Set the [[Construct]] internal method of _obj_ as described in <emu-xref href="#sec-bound-function-exotic-objects-construct-argumentslist-newtarget"></emu-xref>.
          1. Set the [[Prototype]] internal slot of _obj_ to _proto_.
          1. Set the [[Extensible]] internal slot of _obj_ to *true*.
          1. Set the [[BoundTargetFunction]] internal slot of _obj_ to _targetFunction_.
          1. Set the [[BoundThis]] internal slot of _obj_ to the value of _boundThis_.
          1. Set the [[BoundArguments]] internal slot of _obj_ to _boundArgs_.
          1. Return _obj_.