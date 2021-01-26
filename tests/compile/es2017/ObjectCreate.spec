        1. If _internalSlotsList_ was not provided, set _internalSlotsList_ to a new empty List.
        1. Let _obj_ be a newly created object with an internal slot for each name in _internalSlotsList_.
        1. Set _obj_'s essential internal methods to the default ordinary object definitions specified in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots"></emu-xref>.
        1. Set _obj_.[[Prototype]] to _proto_.
        1. Set _obj_.[[Extensible]] to *true*.
        1. Return _obj_.