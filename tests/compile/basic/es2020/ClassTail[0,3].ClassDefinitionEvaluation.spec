        1. Let _lex_ be the LexicalEnvironment of the running execution context.
        1. Let _classScope_ be NewDeclarativeEnvironment(_lex_).
        1. Let _classScopeEnvRec_ be _classScope_'s EnvironmentRecord.
        1. If _classBinding_ is not *undefined*, then
          1. Perform _classScopeEnvRec_.CreateImmutableBinding(_classBinding_, *true*).
        1. If |ClassHeritage_opt| is not present, then
          1. Let _protoParent_ be %Object.prototype%.
          1. Let _constructorParent_ be %Function.prototype%.
        1. Else,
          1. Set the running execution context's LexicalEnvironment to _classScope_.
          1. Let _superclassRef_ be the result of evaluating |ClassHeritage|.
          1. Set the running execution context's LexicalEnvironment to _lex_.
          1. Let _superclass_ be ? GetValue(_superclassRef_).
          1. If _superclass_ is *null*, then
            1. Let _protoParent_ be *null*.
            1. Let _constructorParent_ be %Function.prototype%.
          1. Else if IsConstructor(_superclass_) is *false*, throw a *TypeError* exception.
          1. Else,
            1. Let _protoParent_ be ? Get(_superclass_, *"prototype"*).
            1. If Type(_protoParent_) is neither Object nor Null, throw a *TypeError* exception.
            1. Let _constructorParent_ be _superclass_.
        1. Let _proto_ be OrdinaryObjectCreate(_protoParent_).
        1. If |ClassBody_opt| is not present, let _constructor_ be ~empty~.
        1. Else, let _constructor_ be ConstructorMethod of |ClassBody|.
        1. If _constructor_ is ~empty~, then
          1. If |ClassHeritage_opt| is present, then
            1. Set _constructor_ to the result of parsing the source text
              <pre><code class="javascript">constructor(...args) { super(...args); }</code></pre>
              using the syntactic grammar with the goal symbol |MethodDefinition[~Yield, ~Await]|.
          1. Else,
            1. Set _constructor_ to the result of parsing the source text
              <pre><code class="javascript">constructor() {}</code></pre>
              using the syntactic grammar with the goal symbol |MethodDefinition[~Yield, ~Await]|.
        1. Set the running execution context's LexicalEnvironment to _classScope_.
        1. Let _constructorInfo_ be ! DefineMethod of _constructor_ with arguments _proto_ and _constructorParent_.
        1. Let _F_ be _constructorInfo_.[[Closure]].
        1. Perform MakeConstructor(_F_, *false*, _proto_).
        1. If |ClassHeritage_opt| is present, set _F_.[[ConstructorKind]] to ~derived~.
        1. Perform MakeClassConstructor(_F_).
        1. If _className_ is not *undefined*, then
          1. Perform SetFunctionName(_F_, _className_).
        1. Perform CreateMethodProperty(_proto_, *"constructor"*, _F_).
        1. If |ClassBody_opt| is not present, let _methods_ be a new empty List.
        1. Else, let _methods_ be NonConstructorMethodDefinitions of |ClassBody|.
        1. For each |ClassElement| _m_ in order from _methods_, do
          1. If IsStatic of _m_ is *false*, then
            1. Let _status_ be PropertyDefinitionEvaluation of _m_ with arguments _proto_ and *false*.
          1. Else,
            1. Let _status_ be PropertyDefinitionEvaluation of _m_ with arguments _F_ and *false*.
          1. If _status_ is an abrupt completion, then
            1. Set the running execution context's LexicalEnvironment to _lex_.
            1. Return Completion(_status_).
        1. Set the running execution context's LexicalEnvironment to _lex_.
        1. If _classBinding_ is not *undefined*, then
          1. Perform _classScopeEnvRec_.InitializeBinding(_classBinding_, _F_).
        1. Return _F_.