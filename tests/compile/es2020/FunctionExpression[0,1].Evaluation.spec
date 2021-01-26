        1. Let _scope_ be the running execution context's LexicalEnvironment.
        1. Let _funcEnv_ be NewDeclarativeEnvironment(_scope_).
        1. Let _envRec_ be _funcEnv_'s EnvironmentRecord.
        1. Let _name_ be StringValue of |BindingIdentifier|.
        1. Perform _envRec_.CreateImmutableBinding(_name_, *false*).
        1. Let _closure_ be OrdinaryFunctionCreate(%Function.prototype%, |FormalParameters|, |FunctionBody|, ~non-lexical-this~, _funcEnv_).
        1. Perform MakeConstructor(_closure_).
        1. Perform SetFunctionName(_closure_, _name_).
        1. Set _closure_.[[SourceText]] to the source text matched by |FunctionExpression|.
        1. Perform _envRec_.InitializeBinding(_name_, _closure_).
        1. Return _closure_.