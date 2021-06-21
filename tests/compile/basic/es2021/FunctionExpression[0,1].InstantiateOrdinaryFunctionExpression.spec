        1. Assert: _name_ is not present.
        1. Set _name_ to StringValue of |BindingIdentifier|.
        1. Let _scope_ be the running execution context's LexicalEnvironment.
        1. Let _funcEnv_ be NewDeclarativeEnvironment(_scope_).
        1. Perform _funcEnv_.CreateImmutableBinding(_name_, *false*).
        1. Let _sourceText_ be the source text matched by |FunctionExpression|.
        1. Let _closure_ be OrdinaryFunctionCreate(%Function.prototype%, _sourceText_, |FormalParameters|, |FunctionBody|, ~non-lexical-this~, _funcEnv_).
        1. Perform SetFunctionName(_closure_, _name_).
        1. Perform MakeConstructor(_closure_).
        1. Perform _funcEnv_.InitializeBinding(_name_, _closure_).
        1. Return _closure_.