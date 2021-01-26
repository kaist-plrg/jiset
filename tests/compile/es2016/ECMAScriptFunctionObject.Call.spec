        1. Assert: _F_ is an ECMAScript function object.
        1. If _F_'s [[FunctionKind]] internal slot is `"classConstructor"`, throw a *TypeError* exception.
        1. Let _callerContext_ be the running execution context.
        1. Let _calleeContext_ be PrepareForOrdinaryCall(_F_, *undefined*).
        1. Assert: _calleeContext_ is now the running execution context.
        1. Perform OrdinaryCallBindThis(_F_, _calleeContext_, _thisArgument_).
        1. Let _result_ be OrdinaryCallEvaluateBody(_F_, _argumentsList_).
        1. Remove _calleeContext_ from the execution context stack and restore _callerContext_ as the running execution context.
        1. If _result_.[[Type]] is ~return~, return NormalCompletion(_result_.[[Value]]).
        1. ReturnIfAbrupt(_result_).
        1. Return NormalCompletion(*undefined*).