          1. Let _oldEnv_ be the running execution context's LexicalEnvironment.
          1. Let _V_ be *undefined*.
          1. Let _destructuring_ be IsDestructuring of _lhs_.
          1. If _destructuring_ is *true* and if _lhsKind_ is ~assignment~, then
            1. Assert: _lhs_ is a |LeftHandSideExpression|.
            1. Let _assignmentPattern_ be the parse of the source text corresponding to _lhs_ using |AssignmentPattern| as the goal symbol.
          1. Repeat,
            1. Let _nextResult_ be ? IteratorStep(_iterator_).
            1. If _nextResult_ is *false*, return NormalCompletion(_V_).
            1. Let _nextValue_ be ? IteratorValue(_nextResult_).
            1. If _lhsKind_ is either ~assignment~ or ~varBinding~, then
              1. If _destructuring_ is *false*, then
                1. Let _lhsRef_ be the result of evaluating _lhs_. (It may be evaluated repeatedly.)
            1. Else,
              1. Assert: _lhsKind_ is ~lexicalBinding~.
              1. Assert: _lhs_ is a |ForDeclaration|.
              1. Let _iterationEnv_ be NewDeclarativeEnvironment(_oldEnv_).
              1. Perform BindingInstantiation for _lhs_ passing _iterationEnv_ as the argument.
              1. Set the running execution context's LexicalEnvironment to _iterationEnv_.
              1. If _destructuring_ is *false*, then
                1. Assert: _lhs_ binds a single name.
                1. Let _lhsName_ be the sole element of BoundNames of _lhs_.
                1. Let _lhsRef_ be ! ResolveBinding(_lhsName_).
            1. If _destructuring_ is *false*, then
              1. If _lhsRef_ is an abrupt completion, then
                1. Let _status_ be _lhsRef_.
              1. Else if _lhsKind_ is ~lexicalBinding~, then
                1. Let _status_ be InitializeReferencedBinding(_lhsRef_, _nextValue_).
              1. Else,
                1. Let _status_ be PutValue(_lhsRef_, _nextValue_).
            1. Else,
              1. If _lhsKind_ is ~assignment~, then
                1. Let _status_ be the result of performing DestructuringAssignmentEvaluation of _assignmentPattern_ using _nextValue_ as the argument.
              1. Else if _lhsKind_ is ~varBinding~, then
                1. Assert: _lhs_ is a |ForBinding|.
                1. Let _status_ be the result of performing BindingInitialization for _lhs_ passing _nextValue_ and *undefined* as the arguments.
              1. Else,
                1. Assert: _lhsKind_ is ~lexicalBinding~.
                1. Assert: _lhs_ is a |ForDeclaration|.
                1. Let _status_ be the result of performing BindingInitialization for _lhs_ passing _nextValue_ and _iterationEnv_ as arguments.
            1. If _status_ is an abrupt completion, then
              1. Set the running execution context's LexicalEnvironment to _oldEnv_.
              1. If _iterationKind_ is ~enumerate~, then
                1. Return _status_.
              1. Else,
                1. Assert: _iterationKind_ is ~iterate~.
                1. Return ? IteratorClose(_iterator_, _status_).
            1. Let _result_ be the result of evaluating _stmt_.
            1. Set the running execution context's LexicalEnvironment to _oldEnv_.
            1. If LoopContinues(_result_, _labelSet_) is *false*, then
              1. If _iterationKind_ is ~enumerate~, then
                1. Return Completion(UpdateEmpty(_result_, _V_)).
              1. Else,
                1. Assert: _iterationKind_ is ~iterate~.
                1. Return ? IteratorClose(_iterator_, UpdateEmpty(_result_, _V_)).
            1. If _result_.[[Value]] is not ~empty~, set _V_ to _result_.[[Value]].