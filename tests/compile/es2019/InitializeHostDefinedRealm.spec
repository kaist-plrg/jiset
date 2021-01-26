      1. Let _realm_ be CreateRealm().
      1. Let _newContext_ be a new execution context.
      1. Set the Function of _newContext_ to *null*.
      1. Set the Realm of _newContext_ to _realm_.
      1. Set the ScriptOrModule of _newContext_ to *null*.
      1. Push _newContext_ onto the execution context stack; _newContext_ is now the running execution context.
      1. If the host requires use of an exotic object to serve as _realm_'s global object, let _global_ be such an object created in an implementation-defined manner. Otherwise, let _global_ be *undefined*, indicating that an ordinary object should be created as the global object.
      1. If the host requires that the `this` binding in _realm_'s global scope return an object other than the global object, let _thisValue_ be such an object created in an implementation-defined manner. Otherwise, let _thisValue_ be *undefined*, indicating that _realm_'s global `this` binding should be the global object.
      1. Perform SetRealmGlobalObject(_realm_, _global_, _thisValue_).
      1. Let _globalObj_ be ? SetDefaultGlobalBindings(_realm_).
      1. Create any implementation-defined global object properties on _globalObj_.
      1. Return NormalCompletion(~empty~).