            1. Assert: _constructExpr_ is either a |NewExpression| or a |MemberExpression|.
            1. Assert: _arguments_ is either ~empty~ or an |Arguments|.
            1. Let _ref_ be the result of evaluating _constructExpr_.
            1. Let _constructor_ be ? GetValue(_ref_).
            1. If _arguments_ is ~empty~, let _argList_ be a new empty List.
            1. Else,
              1. Let _argList_ be ? ArgumentListEvaluation of _arguments_.
            1. If IsConstructor(_constructor_) is *false*, throw a *TypeError* exception.
            1. Return ? Construct(_constructor_, _argList_).