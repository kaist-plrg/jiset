        1. Let _callerContext_ be the running execution context.
        1. If _callerContext_ is not already suspended, suspend _callerContext_.
        1. Let _calleeContext_ be a new execution context.
        1. Set the Function of _calleeContext_ to _F_.
        1. Let _calleeRealm_ be _F_.[[Realm]].
        1. Set the Realm of _calleeContext_ to _calleeRealm_.
        1. Set the ScriptOrModule of _calleeContext_ to *null*.
        1. Perform any necessary implementation-defined initialization of _calleeContext_.
        1. Push _calleeContext_ onto the execution context stack; _calleeContext_ is now the running execution context.
        1. [id="step-call-builtin-function-result"] Let _result_ be the Completion Record that is the result of evaluating _F_ in a manner that conforms to the specification of _F_. _thisArgument_ is the *this* value, _argumentsList_ provides the named parameters, and the NewTarget value is *undefined*.
        1. Remove _calleeContext_ from the execution context stack and restore _callerContext_ as the running execution context.
        1. Return _result_.