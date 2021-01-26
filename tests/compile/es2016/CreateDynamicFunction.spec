            1. If _newTarget_ is *undefined*, let _newTarget_ be _constructor_.
            1. If _kind_ is `"normal"`, then
              1. Let _goal_ be the grammar symbol |FunctionBody|.
              1. Let _parameterGoal_ be the grammar symbol |FormalParameters|.
              1. Let _fallbackProto_ be `"%FunctionPrototype%"`.
            1. Else,
              1. Let _goal_ be the grammar symbol |GeneratorBody|.
              1. Let _parameterGoal_ be the grammar symbol |FormalParameters[Yield]|.
              1. Let _fallbackProto_ be `"%Generator%"`.
            1. Let _argCount_ be the number of elements in _args_.
            1. Let _P_ be the empty String.
            1. If _argCount_ = 0, let _bodyText_ be the empty String.
            1. Else if _argCount_ = 1, let _bodyText_ be _args_[0].
            1. Else _argCount_ > 1,
              1. Let _firstArg_ be _args_[0].
              1. Let _P_ be ? ToString(_firstArg_).
              1. Let _k_ be 1.
              1. Repeat, while _k_ < _argCount_-1
                1. Let _nextArg_ be _args_[_k_].
                1. Let _nextArgString_ be ? ToString(_nextArg_).
                1. Let _P_ be the result of concatenating the previous value of _P_, the String `","` (a comma), and _nextArgString_.
                1. Increase _k_ by 1.
              1. Let _bodyText_ be _args_[_k_].
            1. Let _bodyText_ be ? ToString(_bodyText_).
            1. Let _parameters_ be the result of parsing _P_, interpreted as UTF-16 encoded Unicode text as described in <emu-xref href="#sec-ecmascript-language-types-string-type"></emu-xref>, using _parameterGoal_ as the goal symbol. Throw a *SyntaxError* exception if the parse fails.
            1. Let _body_ be the result of parsing _bodyText_, interpreted as UTF-16 encoded Unicode text as described in <emu-xref href="#sec-ecmascript-language-types-string-type"></emu-xref>, using _goal_ as the goal symbol. Throw a *SyntaxError* exception if the parse fails.
            1. If _bodyText_ is strict mode code, then let _strict_ be *true*, else let _strict_ be *false*.
            1. If any static semantics errors are detected for _parameters_ or _body_, throw a *SyntaxError* or a *ReferenceError* exception, depending on the type of the error. If _strict_ is *true*, the Early Error rules for <emu-grammar>StrictFormalParameters : FormalParameters</emu-grammar> are applied. Parsing and early error detection may be interweaved in an implementation dependent manner.
            1. If ContainsUseStrict of _body_ is *true* and IsSimpleParameterList of _parameters_ is *false*, throw a *SyntaxError* exception.
            1. If any element of the BoundNames of _parameters_ also occurs in the LexicallyDeclaredNames of _body_, throw a *SyntaxError* exception.
            1. If _body_ Contains |SuperCall| is *true*, throw a *SyntaxError* exception.
            1. If _parameters_ Contains |SuperCall| is *true*, throw a *SyntaxError* exception.
            1. If _body_ Contains |SuperProperty| is *true*, throw a *SyntaxError* exception.
            1. If _parameters_ Contains |SuperProperty| is *true*, throw a *SyntaxError* exception.
            1. If _kind_ is `"generator"`, then
              1. If _parameters_ Contains |YieldExpression| is *true*, throw a *SyntaxError* exception.
            1. If _strict_ is *true*, then
              1. If BoundNames of _parameters_ contains any duplicate elements, throw a *SyntaxError* exception.
            1. Let _proto_ be ? GetPrototypeFromConstructor(_newTarget_, _fallbackProto_).
            1. Let _F_ be FunctionAllocate(_proto_, _strict_, _kind_).
            1. Let _realmF_ be the value of _F_'s [[Realm]] internal slot.
            1. Let _scope_ be _realmF_.[[GlobalEnv]].
            1. Perform FunctionInitialize(_F_, ~Normal~, _parameters_, _body_, _scope_).
            1. If _kind_ is `"generator"`, then
              1. Let _prototype_ be ObjectCreate(%GeneratorPrototype%).
              1. Perform DefinePropertyOrThrow(_F_, `"prototype"`, PropertyDescriptor{[[Value]]: _prototype_, [[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false*}).
            1. Else, perform MakeConstructor(_F_).
            1. Perform SetFunctionName(_F_, `"anonymous"`).
            1. Return _F_.