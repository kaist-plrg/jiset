          1. Let _weakRef_ be the *this* value.
          1. Perform ? RequireInternalSlot(_weakRef_, [[WeakRefTarget]]).
          1. Return ! WeakRefDeref(_weakRef_).