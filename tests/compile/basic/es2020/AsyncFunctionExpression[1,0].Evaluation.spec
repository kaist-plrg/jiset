        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _funcEnv_ be ! NewDeclarativeEnvironment(_scope_).
        1. Let _envRec_ be _funcEnv_'s EnvironmentRecord.
        1. Let _name_ be StringValue of |BindingIdentifier|.
        1. Perform ! _envRec_.CreateImmutableBinding(_name_, *false*).
        1. Let _closure_ be ! OrdinaryFunctionCreate(%AsyncFunction.prototype%, |FormalParameters|, |AsyncFunctionBody|, ~non-lexical-this~, _funcEnv_).
        1. Perform ! SetFunctionName(_closure_, _name_).
        1. Perform ! _envRec_.InitializeBinding(_name_, _closure_).
        1. Set _closure_.[[SourceText]] to the source text matched by |AsyncFunctionExpression|.
        1. Return _closure_.