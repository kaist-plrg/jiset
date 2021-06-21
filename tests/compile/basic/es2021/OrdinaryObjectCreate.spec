        1. Let _internalSlotsList_ be « [[Prototype]], [[Extensible]] ».
        1. If _additionalInternalSlotsList_ is present, append each of its elements to _internalSlotsList_.
        1. Let _O_ be ! MakeBasicObject(_internalSlotsList_).
        1. Set _O_.[[Prototype]] to _proto_.
        1. Return _O_.