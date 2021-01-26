        1. Assert: _internalSlotsList_ is a List of internal slot names.
        1. Let _obj_ be a newly created object with an internal slot for each name in _internalSlotsList_.
        1. Set _obj_'s essential internal methods to the default ordinary object definitions specified in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots"></emu-xref>.
        1. Assert: If the caller will not be overriding both _obj_'s [[GetPrototypeOf]] and [[SetPrototypeOf]] essential internal methods, then _internalSlotsList_ contains [[Prototype]].
        1. Assert: If the caller will not be overriding all of _obj_'s [[SetPrototypeOf]], [[IsExtensible]], and [[PreventExtensions]] essential internal methods, then _internalSlotsList_ contains [[Extensible]].
        1. If _internalSlotsList_ contains [[Extensible]], set _obj_.[[Extensible]] to *true*.
        1. Return _obj_.