        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _funcEnv_ be ! NewDeclarativeEnvironment(_scope_).
        1. Let _name_ be StringValue of |BindingIdentifier|.
        1. Perform ! _funcEnv_.CreateImmutableBinding(_name_, *false*).
        1. Let _sourceText_ be the source text matched by |AsyncFunctionExpression|.
        1. Let _closure_ be ! OrdinaryFunctionCreate(%AsyncFunction.prototype%, _sourceText_, |FormalParameters|, |AsyncFunctionBody|, ~non-lexical-this~, _funcEnv_).
        1. Perform ! SetFunctionName(_closure_, _name_).
        1. Perform ! _funcEnv_.InitializeBinding(_name_, _closure_).
        1. Return _closure_.