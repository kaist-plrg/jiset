        1. If the function code for this |AsyncGeneratorExpression| is strict mode code, let _strict_ be *true*. Otherwise let _strict_ be *false*.
        1. Let _scope_ be the running execution context's LexicalEnvironment.
        1. Let _funcEnv_ be ! NewDeclarativeEnvironment(_scope_).
        1. Let _envRec_ be _funcEnv_'s EnvironmentRecord.
        1. Let _name_ be StringValue of |BindingIdentifier|.
        1. Perform ! _envRec_.CreateImmutableBinding(_name_).
        1. Let _closure_ be ! AsyncGeneratorFunctionCreate(~Normal~, |FormalParameters|, |AsyncGeneratorBody|, _funcEnv_, _strict_).
        1. Let _prototype_ be ! ObjectCreate(%AsyncGeneratorPrototype%).
        1. Perform ! DefinePropertyOrThrow(_closure_, `"prototype"`, PropertyDescriptor { [[Value]]: _prototype_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false* }).
        1. Perform ! SetFunctionName(_closure_, _name_).
        1. Perform ! _envRec_.InitializeBinding(_name_, _closure_).
        1. Return _closure_.