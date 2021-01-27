          1. Let _expr_ be CoveredCallExpression of |CoverCallExpressionAndAsyncArrowHead|.
          1. Let _memberExpr_ be the |MemberExpression| of _expr_.
          1. Let _arguments_ be the |Arguments| of _expr_.
          1. Let _ref_ be the result of evaluating _memberExpr_.
          1. Let _func_ be ? GetValue(_ref_).
          1. If Type(_ref_) is Reference and IsPropertyReference(_ref_) is *false* and GetReferencedName(_ref_) is `"eval"`, then
            1. If SameValue(_func_, %eval%) is *true*, then
              1. Let _argList_ be ? ArgumentListEvaluation(_arguments_).
              1. If _argList_ has no elements, return *undefined*.
              1. Let _evalText_ be the first element of _argList_.
              1. If the source code matching this |CallExpression| is strict mode code, let _strictCaller_ be *true*. Otherwise let _strictCaller_ be *false*.
              1. Let _evalRealm_ be the current Realm Record.
              1. Perform ? HostEnsureCanCompileStrings(_evalRealm_, _evalRealm_).
              1. Return ? PerformEval(_evalText_, _evalRealm_, _strictCaller_, *true*).
          1. If Type(_ref_) is Reference, then
            1. If IsPropertyReference(_ref_) is *true*, then
              1. Let _thisValue_ be GetThisValue(_ref_).
            1. Else the base of _ref_ is an Environment Record,
              1. Let _refEnv_ be GetBase(_ref_).
              1. Let _thisValue_ be _refEnv_.WithBaseObject().
          1. Else Type(_ref_) is not Reference,
            1. Let _thisValue_ be *undefined*.
          1. Let _thisCall_ be this |CallExpression|.
          1. Let _tailCall_ be IsInTailPosition(_thisCall_).
          1. Return ? EvaluateDirectCall(_func_, _thisValue_, _arguments_, _tailCall_).