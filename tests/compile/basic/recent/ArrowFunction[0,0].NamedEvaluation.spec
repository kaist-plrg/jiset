        1. Let _scope_ be the LexicalEnvironment of the running execution context.
        1. Let _sourceText_ be the source text matched by |ArrowFunction|.
        1. Let _parameters_ be CoveredFormalsList of |ArrowParameters|.
        1. [id="step-arrowfunction-evaluation-functioncreate"] Let _closure_ be OrdinaryFunctionCreate(%Function.prototype%, _sourceText_, _parameters_, |ConciseBody|, ~lexical-this~, _scope_).
        1. Perform SetFunctionName(_closure_, _name_).
        1. Return _closure_.