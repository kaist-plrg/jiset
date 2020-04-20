SpecExtractor.loadSpec(() => {
  with(SpecExtractor) {
    // JSZip setting
    let zip = new JSZip();

    // Spec components
    let methodSet = new Set();
    let globalMethods = [];
    let grammar;
    let tys = {};

    // Global Methods
    ////////////////////////////////////////////////////////////////////////////
    let builtinFunctionObjectCall;
    let arrayData;
    let arrayCount = 0;
    function addMethod(name, length, data) {
      name = name.replace("GLOBAL.Generator.prototype.", "GLOBAL.INTRINSIC_GeneratorPrototype.")
      name = name.replace("GLOBAL.AsyncGenerator.prototype.", "GLOBAL.INTRINSIC_AsyncGeneratorPrototype.")
      name = name.replace(/%[^%]+%/g, x => 'INTRINSIC_' + x.substring(1, x.length-1))
      name = name.replace(/\[@@[^%]+\]/g, x => '.SYMBOL_' + x.substring(3, x.length-1))
      // not yet handle functions
      if (name == 'GLOBAL.Array.prototype.sort'
        || name == 'GLOBAL.IfAbruptRejectPromise'
        || /[-\[\]@]/g.test(name)
      ) {
        error(`[NotYetHandle] ${name}`);
        return;
      } else if (name == 'Identifier0StringValue0') {
        data.params = ['this', 'IdentifierName'];
      } else if (name == 'ListIteratornext') {
        data.params = ['this'];
      } else if (name == 'ArrayBindingPattern0BoundNames2') {
        let params = data.params;
        data.params = ['this'];
        addMethod('ArrayBindingPattern0BoundNames0', length, data)
        data.params = params;
      } else if (name == 'ArrayBindingPattern2BoundNames2') {
        let params = data.params;
        data.params = ['this', 'BindingElementList'];
        addMethod('ArrayBindingPattern2BoundNames0', length, data)
        data.params = params;
      } else if (name == 'ArrowParameters0IteratorBindingInitialization0') {
        let params = data.params;
        data.params = ['BindingIdentifier', '_', 'iteratorRecord', 'environment'];
        addMethod('BindingIdentifier0IteratorBindingInitialization0', length, data)
        data.params = ['BindingIdentifier', 'iteratorRecord', 'environment'];
        addMethod('BindingIdentifier1IteratorBindingInitialization0', length, data)
        addMethod('BindingIdentifier2IteratorBindingInitialization0', length, data)
        data.params = params;
      } else if (name == 'CoverParenthesizedExpressionAndArrowParameterList0CoveredFormalsList0') {
        addMethod('CoverParenthesizedExpressionAndArrowParameterList1CoveredFormalsList0', length, data);
      } else if (name == 'AbstractRelationalComparison') {
        data.params = ['x', 'y', 'LeftFirst'];
        data.steps = [{
            'tokens' : ["If", {id: 'LeftFirst'}, "is", "not", "present", ",", "let", {id: 'LeftFirst'}, "be", {value:'true'}, "."]
        }].concat(data.steps);
      } else if (name == 'AbstractEqualityComparison') {
        data.params = ["x", "y"];
        data.steps[7] = {
            'tokens' :  ["If", "Type", "(", {id: 'x'}, ")", "is", "either", "String", ",", "Number", ",", "or", "Symbol", "and", "Type", "(", {id: 'y'}, ")", "is", "Object", ",", "return", "the", "result", "of", "the", "comparison", {id: 'x'}, "=", "=", "?", "ToPrimitive", "(", {id: 'y'}, ")", "."]
        };
        data.steps[8] = {
            'tokens' : ["If", "Type", "(", {id: 'x'}, ")", "is", "Object", "and", "Type", "(", {id: 'y'}, ")", "is", "either", "String", ",", "Number", ",", "or", "Symbol", ",", "return", "the", "result", "of", "the", "comparison", "?", "ToPrimitive", "(", {id: 'x'}, ")", "=", "=", {id: 'y'}, "."]
        };
      } else if (name == "StrictEqualityComparison") {
        data.params = ["x", "y"];
      } else if (name == 'Evaluation') {
        data.params = ['this', 'A', 'B'];
        data.steps[6] = {
          tokens: ['Return', {id: 'lnum'}, '', {id: 'rnum'}, '.']
        };
        data.steps[6].tokens[2] = '&';
        addMethod('BitwiseANDExpression1Evaluation0', length, data);
        data.steps[6].tokens[2] = '^';
        addMethod('BitwiseXORExpression1Evaluation0', length, data);
        data.steps[6].tokens[2] = '|';
        addMethod('BitwiseORExpression1Evaluation0', length, data);
        return;
      } else if (name == 'GLOBAL.Array.prototype.push' || name == 'GLOBAL.Array.prototype.concat' || name == 'GLOBAL.Array.prototype.unshift' || name == 'GLOBAL.Number.prototype.toString' || name == 'GLOBAL.String.fromCodePoint' || name == 'GLOBAL.String.prototype.concat' || name == 'GLOBAL.String.fromCharCode') {
        length = 1;
      } else if (name == 'GLOBAL.Object.assign') {
        length = 2;
      } else if (name == 'GLOBAL.Array.from') {
        data.steps[7].tokens[7].steps[4].tokens[2].steps[5].tokens[6].steps.length = 2;
      } else if (name == 'GLOBAL.Array.prototype.sort') {
        return;
      } else if (name == 'SortCompare') {
        return;
      } else if (name == 'GLOBAL.Array') {
        if (arrayCount == 0) {
          arrayData = data;
          arrayData.steps = [{
            tokens: ['If', 'the', 'length', 'of', {id: 'argumentsList'}, '=', {value:'0'}, ',', {steps: data.steps}]
          }];
          arrayCount++;
          return;
        } else if (arrayCount == 1) {
          arrayData.steps = arrayData.steps.concat([{
            tokens: ['Else', 'if', 'the', 'length', 'of', {id: 'argumentsList'}, '=', {value:'1'}, ',', {steps: data.steps}]
          }]);
          arrayCount++;
          return;
        } else {
          arrayData.steps = arrayData.steps.concat([{
            tokens: ['Else', ',', {steps: data.steps}]
          }]);
          data = arrayData;
        }
      } else if (name == 'UnaryExpression3Evaluation0') {
        let newSteps = data.steps.slice(0, 1);
        newSteps.push({
          tokens: ['ReturnIfAbrupt', '(', {'id': 'val'}, ')', '.']
        });
        newSteps = newSteps.concat(data.steps.slice(1));
        data.steps = newSteps;
      } else if (name == 'CallExpression0CoveredCallExpression0') {
        data.steps[0].tokens[7] = 'this';
        addMethod('CoverCallExpressionAndAsyncArrowHead0CoveredCallExpression0', length, data);
        return;
      } else if (name == 'CallExpression0Evaluation0') {
        let newSteps = data.steps.slice(0, 4);
        newSteps.push({
          tokens: ['ReturnIfAbrupt', '(', {'id': 'ref'}, ')', '.']
        });
        newSteps = newSteps.concat(data.steps.slice(4));
        data.steps = newSteps;
      } else if (name == 'EvaluateCall') {
        data.steps = [{
          tokens: ['ReturnIfAbrupt', '(', {'id': 'ref'}, ')', '.']
        }].concat(data.steps);
      } else if (name == 'GeneratorYield') {
        data.steps[8] = {
            tokens: ['ReturnCont', {'id': 'genContext'}, '.', '[', '[', 'ReturnCont', ']', ']', 'to', 'NormalCompletion', '(', {'id': 'iterNextObj'}, ')', '.']
        };
      } else if (name == 'AsyncGeneratorYield') {
        data.steps[8] = {
            tokens: ['ReturnCont', {'id': 'genContext'}, '.', '[', '[', 'ReturnCont', ']', ']', 'to', '!', 'AsyncGeneratorResolve', '(', {'id': 'generator'}, ',', {'id': 'value'}, ',', {'value': 'false'}, ')', '.']
        };
      } else if (name == 'AsyncGeneratorResumeNext') {
        data.steps[9].tokens[8].steps[1].tokens[6].steps[0].tokens[12].steps[1] = { //specError#1
            tokens: ['Let', {id: 'promise'}, 'be', '?', 'PromiseResolve', '(', '%', 'Promise', '%', ',', {id: 'completion'}, '.', '[', '[', 'Value', ']', ']', ')', '.']
        };
      } else if (name == 'GeneratorStart') {
        data.steps[3].tokens[24].steps[7].tokens[2].steps[1].tokens[0] = 'ReturnCont';
        data.steps[3].tokens[24].steps[8].tokens[0] = 'ReturnCont';
      } else if (name == 'AsyncGeneratorStart') {
        data.steps[4].tokens[24].steps[5].tokens[2].steps[1].tokens[13].steps[0].tokens[0] = 'ReturnCont';
        data.steps[4].tokens[24].steps[6].tokens[0] = 'ReturnCont';
      } else if (name == 'AsyncFunctionStart') {
        data.steps[2].tokens[24].steps[6].tokens[0] = 'ReturnCont';
      } else if (name == 'AsyncFromSyncIteratorContinuation') {
          data.steps[4] = { // specError#1
              tokens: ['Let', {id: 'valueWrapper'}, 'be', '?', 'PromiseResolve', '(', '%', 'Promise', '%', ',', {id: 'value'}, ')', '.']
          };
      } else if (name == 'ForInOfHeadEvaluation') {
          data.steps[6].tokens[2].steps[0] = { //specError#2
              tokens: ['Assert', ':', {id: 'iterationKind'}, 'is', {'const' :'iterate'}, 'or', {'const': 'async-iterate'}, '.']
          };
      } else if (name == 'MemberExpression1Evaluation0') {
        addMethod('CallExpression3Evaluation0', length, data);
      } else if (name == 'MemberExpression2Evaluation0') {
        addMethod('CallExpression4Evaluation0', length, data);
      } else if (name == 'EqualityExpression2Evaluation0') { //specError#3
        let newSteps = data.steps.slice(0, 5);
        newSteps.push({
          tokens: ['ReturnIfAbrupt', '(', {'id': 'r'}, ')', '.']
        });
        newSteps = newSteps.concat(data.steps.slice(5));
        data.steps = newSteps;
      } else if (name == 'FunctionExpression0IsFunctionDefinition1') {
        let params = data.params;
        data.params = ['this', 'FormalParameters', 'FunctionBody'];
        addMethod('FunctionExpression0IsFunctionDefinition0', length, data)
        data.params = params;
      } else if (name == 'ClassTail0ClassDefinitionEvaluation3') {
        let newSteps = data.steps.slice(0, 12);
        newSteps.push({
          tokens: ['ReturnIfAbrupt', '(', {'id': 'constructorInfo'}, ')', '.']
        });
        newSteps = newSteps.concat(data.steps.slice(12));
        data.steps = newSteps;
      } else if (methodSet.has(name)) switch (name) {
        case 'MakeArgGetter':
          name = 'ArgGetter';
          data.params = ['_', '_', '_', 'f'];
          break;
        case 'MakeArgSetter':
          name = 'ArgSetter';
          data.params = ['_', 'argumentsList', '_', 'f'];
          data.steps = [{
            tokens: ['Let', {id: 'value'}, 'be', {id: 'argumentsList'}, '[', '0', ']']
          }].concat(data.steps);
          break;
        case 'Await':
          data.params = ['value'];
          data.steps[1] = { // specError#1
              tokens: ['Let', {id: 'promise'}, 'be', '?', 'PromiseResolve', '(', '%', 'Promise', '%', ',', {id: 'value'}, ')', '.']
          };
          data.steps[11].tokens[0] = 'ReturnCont';
          break;
        // XXX Ambiguous semantics
        case 'IterationStatement12VarScopedDeclarations0':
          break;
        default:
          error(`[AlreadyExist] ${name}`);
          return;
      } else if (name == 'GLOBAL.NativeError') {
        let errList =  [
          'EvalError',
          'RangeError',
          'ReferenceError',
          'SyntaxError',
          'TypeError',
          'URIError',
        ];
        for (let errName of errList) {
          data.steps[2].tokens[8] = {'code':`"%${errName}Prototype%"`};
          addMethod('GLOBAL.' + errName, length, data);
        }
        return;
      } else if (name == 'GLOBAL.Function' || name == 'GLOBAL.GeneratorFunction' || name == 'GLOBAL.AsyncFunction' || name == 'GLOBAL.AsyncGeneratorFunction') {
        data.steps = data.steps.slice(5)
      } else if (name == 'BuiltinFunctionObject.Call') {
        builtinFunctionObjectCall = data;
      } else if (name == 'BuiltinFunctionObject.Construct') {
        let steps = [];
        for (let step of builtinFunctionObjectCall.steps) {
          steps.push(step);
        }
        steps[9] = data.steps[0];
        data.steps = steps;
      }
      methodSet.add(name);
      globalMethods.push(name);
      data.length = length;
      data.filename = `es2019/algorithm/${name}.json`;
      save(zip, data, data.filename);
    }
    let globalElementIds = [
      "await", // 6.2.3.1,
      'sec-updateempty', // 6.2.3.4
      'sec-reference-specification-type', // 6.2.4
      'sec-property-descriptor-specification-type', // 6.2.5
      'sec-data-blocks', // 6.2.7
      'sec-abstract-operations', // 7
      'sec-lexical-environment-operations', // 8.1.2
      'sec-code-realms', // 8.2
      'sec-execution-contexts', // 8.3
      'sec-jobs-and-job-queues', // 8.4
      'sec-initializehostdefinedrealm', // 8.5
      'sec-runjobs', // 8.6
      'sec-agents', // 8.7
      'sec-source-text', // 10.1
      'sec-flattenintoarray', // 22.1.3.10.1
      'sec-add-entries-from-iterable', // 23.1.1.2
      'sec-async-from-sync-iterator-objects', // 25.1.4
      'sec-generator-abstract-operations', // 25.4.3
      'sec-asyncgenerator-abstract-operations', // 25.5.3
      'sec-promise-abstract-operations', // 25.6.1
      'sec-promise-jobs', // 25.6.2
      'sec-promise-resolve', // 25.6.4.5
      'sec-performpromisethen', // 25.6.5.4.1
      'sec-async-functions-abstract-operations' // 25.7.5
    ];
    for (let id of globalElementIds) {
      for (let elem of getElem(id).getElementsByTagName('emu-alg')) {
        let algo = getAlgo(elem, grammar);
        if (algo) {
          addMethod(algo.name, algo.length, algo.body);
        } else {
          error(elem);
        }
      }
    }

    // Constants
    ////////////////////////////////////////////////////////////////////////////
    let cset = new Set();
    for (elem of document.getElementsByTagName('emu-const')) {
      cset.add(elem.innerText.replace(/-/g, ""));
    }
    let consts = Array.from(cset);

    // Grammar
    ////////////////////////////////////////////////////////////////////////////
    // remove default white spcae and line terminators
    let spaceNames = new Set(['WhiteSpace', 'LineTerminator', 'LineTerminatorSequence', 'Comment'])
    let removedElems = [];
    for (let elem of document.getElementsByTagName('emu-production')) {
      if (spaceNames.has(elem.getAttribute('name'))) removedElems.push(elem);
    }
    removedElems.forEach(elem => elem.remove());

    // extract productions
    let lexProds = getSection('sec-lexical-grammar');
    let notCodeP = lexProds[79];
    notCodeP.rhsList[0].tokens[0] = notCodeP.rhsList[0].tokens[0].base;
    let codeP = lexProds[80];
    codeP.rhsList[0].tokens[0] = codeP.rhsList[0].tokens[0].base;
    /*lexProds = lexProds.concat([
      'prod-NotEscapeSequence',
      'prod-NotCodePoint',
      'prod-CodePoint'
    ].map(getProd));*/

    // handling nearest `if` for `else` statements
    (() => {
      let ifStmt = document.getElementsByName('IfStatement')[16];
      let x = ifStmt.children[2];
      let y = ifStmt.children[3];
      ifStmt.children[2].remove;
      ifStmt.children[2].remove;
      ifStmt.append(y, x);
    })();

    let prods = [];
    prods = prods.concat(getSection('sec-expressions'));
    prods = prods.concat(getSection('sec-statements'));
    prods = prods.concat(getSection('sec-functions-and-classes'));
    prods = prods.concat(getSection('sec-scripts-and-modules'));
    /*prods = prods.concat([
      'prod-AsyncGeneratorMethod',
      'prod-AsyncGeneratorDeclaration',
      'prod-AsyncGeneratorExpression',
      'prod-AsyncGeneratorBody',
      'prod-AssignmentRestProperty',
      'prod-SubstitutionTemplate',
      'prod-BindingRestProperty'
    ].map(getProd));*/

    // grammar
    grammar = new Grammar(lexProds, prods)

    // Grammar Methods
    ////////////////////////////////////////////////////////////////////////////
    let grammarSections = [
      'sec-ecmascript-language-expressions', // 12
      'sec-ecmascript-language-statements-and-declarations', // 13
      'sec-ecmascript-language-functions-and-classes', // 14
      'sec-scripts' // 15.1
    ];
    let succ = 0;
    let fail = 0;
    for (let id of grammarSections) {
      let section = document.getElementById(id);
      for (let elem of section.getElementsByTagName('emu-alg')) {
        let algo = getAlgo(elem, grammar);
        if (algo) {
          if (algo.name == "StatementRules") algo.name = "HasCallInTailPosition";
          let p = elem.parentElement.children[1];
          let moreParams = [];
          if (p.tagName == 'P' && p.innerText.startsWith('With parameter')) {
            for (let x of p.getElementsByTagName('var')) {
              moreParams.push(x.innerText)
            }
          }
          let grammarElem = elem.previousElementSibling;
          if (grammarElem.tagName == 'EMU-GRAMMAR') {
            let rules = [];
            for (let prod of grammarElem.getElementsByTagName('emu-production')) {
              for (let rhsElem of prod.getElementsByTagName('emu-rhs')) {
                let lhsName = prod.getAttribute('name');
                let rhs = Rhs.from(rhsElem);
                let name = getRhsCaseName(lhsName, rhs)
                if (name in grammar.idxMap) {
                  let obj = grammar.idxMap[name];
                  let type = `${lhsName}${obj.idx}`;
                  let algoName = `${algo.name}${obj.subIdx}`;
                  algo.body.params = [];
                  for (let token of rhs.tokens) {
                    if (token instanceof Nonterminal && token.name !== 'LineTerminator')
                      algo.body.params.push(token.name);
                  }
                  algo.body.params = [ 'this' ].concat(algo.body.params);
                  algo.body.params = algo.body.params.concat(moreParams);
                if (algo.body.params.indexOf(lhsName) === -1) {
                  function replaceStep (steps, name) {
                      return steps.map(function(item) {
                          if (item.hasOwnProperty('steps')) {
                              return { steps: replaceStep(item['steps'], name) };
                        } else if (item.hasOwnProperty('tokens')) {
                            return { tokens: replaceStep(item['tokens'], name) };
                        } else if (item.nt === name) {
                            return 'this';
                        } else {
                            return item;
                        }
                    });
                  };
                  algo.body.steps = replaceStep(algo.body.steps, lhsName);
                }
                  addMethod(type + algoName, algo.length, algo.body);
                } else {
                  error(name);
                  error(elem);
                }
              }
            }
          } else {
            addMethod(algo.name, algo.length, algo.body);
          }
        } else {
          console.log(elem);
        }
      }
    }

    // handling undefined static semantics of Script : [empty]
    let returnEmptyListAlgoBody = {
      kind: "StaticSemantics",
      params: [],
      steps: [
        {
          tokens: ["Return", "a", "new", "empty", "List", "."]
        }
      ]
    }
    function addOmittedScriptSemantics(name) {
      let algoName = name + '0';
      addMethod('Script0' + algoName, 0, returnEmptyListAlgoBody);
    }
    addOmittedScriptSemantics("LexicallyDeclaredNames")
    addOmittedScriptSemantics("VarDeclaredNames")
    addOmittedScriptSemantics("LexicallyScopedDeclarations")
    addOmittedScriptSemantics("VarScopedDeclarations")

    // Types
    ////////////////////////////////////////////////////////////////////////////
    let envTyMap = {
      DeclarativeEnvironmentRecord: [
        'sec-declarative-environment-records' // 8.1.1.1
      ],
      ObjectEnvironmentRecord: [
        'sec-object-environment-records' // 8.1.1.2
      ],
      FunctionEnvironmentRecord: [
        'sec-function-environment-records' // 8.1.1.3
      ],
      GlobalEnvironmentRecord: [
        'sec-global-environment-records' // 8.1.1.4
      ],
      ModuleEnvironmentRecord: [
        'sec-module-environment-records' // 8.1.1.5
      ]
    }
    for (let tname in envTyMap) {
      let methods = {};
      if (tname == 'FunctionEnvironmentRecord'
        || tname == 'ModuleEnvironmentRecord') {
        let dec = tys['DeclarativeEnvironmentRecord'];
        for (methodName in dec) {
          methods[methodName] = dec[methodName];
        }
      }
      for (let id of envTyMap[tname]) {
        for (let elem of document.getElementById(id).getElementsByTagName('emu-alg')) {
          let algo = getAlgo(elem, grammar);
          if (algo) {
            let name = `${tname}.${algo.name}`
            methods[algo.name] = name;
            algo.body.params = [ 'this' ].concat(algo.body.params);
            addMethod(name, algo.length, algo.body);
          } else {
            error(elem);
          }
        }
      }
      tys[tname] = methods;
    }

    let objTyMap = {
      OrdinaryObject: [
        'sec-ordinary-object-internal-methods-and-internal-slots' // 9.1
      ],
      ECMAScriptFunctionObject: [
        'sec-ecmascript-function-objects' // 9.2
      ],
      BuiltinFunctionObject: [
        'sec-built-in-function-objects' // 9.3
      ],
      BoundFunctionExoticObject: [
        'sec-bound-function-exotic-objects' // 9.4.1
      ],
      ArrayExoticObject: [
        'sec-array-exotic-objects' // 9.4.2
      ],
      StringExoticObject: [
        'sec-string-exotic-objects' // 9.4.3
      ],
      ArgumentsExoticObject: [
        'sec-arguments-exotic-objects' // 9.4.4
      ],
      IntegerIndexedExoticObject: [
        'sec-integer-indexed-exotic-objects' // 9.4.5
      ],
      ModuleNamespaceExoticObject: [
        'sec-module-namespace-exotic-objects' // 9.4.6
      ],
      ImmutablePrototypeExoticObject: [
        'sec-immutable-prototype-exotic-objects' // 9.4.7
      ],
      ProxyExoticObject: [
        'sec-proxy-object-internal-methods-and-internal-slots' // 9.5
      ]
    }
    for (let tname in objTyMap) {
      let methods = {};
      if (tname != "OrdinaryObject") {
        let ord = tys["OrdinaryObject"];
        for (methodName in ord) {
          methods[methodName] = ord[methodName];
        }
      }
      for (let id of objTyMap[tname]) {
        for (let elem of document.getElementById(id).getElementsByTagName('emu-alg')) {
          let algo = getAlgo(elem, grammar);
          if (algo) {
            let name;
            if (algo.name.startsWith('[[')) {
              let methodName = algo.name.substring(2, algo.name.length - 2);
              name = `${tname}.${methodName}`;
              methods[methodName] = name;

              let thisName;
              if (tname == 'ArgumentsExoticObject') {
                thisName = 'args';
              } else {
                thisName = elem.previousElementSibling.getElementsByTagName('var')[0].innerText;
              }
              algo.body.params = [ thisName ].concat(algo.body.params);

            } else {
              name = algo.name;
            }
            addMethod(name, algo.length, algo.body);
          } else {
            error(elem);
          }
        }
      }
      tys[tname] = methods;
    }

    // Symbols
    ////////////////////////////////////////////////////////////////////////////
    let symbols = {};
    let symbolList = getElem('table-1').getElementsByTagName('tr')
    for (let i = 1; i < symbolList.length; i++) {
      let tr = symbolList[i];
      let symbol = tr.children[0].innerText;
      let desc = tr.children[1].innerText;
      symbols[symbol] = desc.substring(1, desc.length - 1);
    }

    // Intrinsics
    ////////////////////////////////////////////////////////////////////////////
    let intrinsics = {};
    let intrinsicsList = getElem('table-7').getElementsByTagName('tr')
    for (let i = 1; i < intrinsicsList.length; i++) {
      let tr = intrinsicsList[i];
      let intrinsic = tr.children[0].innerText;
      let property = tr.children[1].innerText;
      if (property == '') property = 'INTRINSIC_' + intrinsic.substring(1, intrinsic.length - 1);
      intrinsics[intrinsic] = `GLOBAL.${property}`;
    }

    // Global Objects
    ////////////////////////////////////////////////////////////////////////////
    let globalObjectMethodIds = [
      "await", // 6.2.3.1
      'sec-global-object', // 18
      'sec-object-objects', // 19.1
      'sec-function-objects', // 19.2
      'sec-boolean-objects', // 19.3
      'sec-symbol-objects', // 19.4
      'sec-error-objects', // 19.5
      'sec-number-objects', // 20.1
      // 'sec-math-object', // 20.2
      // 'sec-date-objects', // 20.3
      'sec-string-objects', // 21.1
      // 'sec-regexp-regular-expression-objects', // 21.1
      'sec-array-objects', // 22.1
      // 'sec-typedarray-objects', // 22.2
      'sec-map-objects', // 23.1
      'sec-set-objects', // 23.2
      'sec-weakmap-objects', // 23.3
      'sec-weakset-objects', // 23.4
      // 'sec-structured-data', // 24
      'sec-control-abstraction-objects', // 25
      // 'sec-reflection' // 26
    ];
    for (let id of globalObjectMethodIds) {
      for (let elem of getElem(id).getElementsByTagName('emu-alg')) {
        let algo = getAlgo(elem, grammar);
        if (algo) {
          if (algo.name.startsWith('Propertiesof')) {
            let prev = elem.previousElementSibling;
            algo.name = prev.getElementsByTagName('dfn')[0].innerText;
            algo.body.params = ['value'];
            addMethod(algo.name, algo.length, algo.body);
          } else switch (algo.body.kind) {
            case 'Method':
              if (
                algo.name == 'CreateStringIterator' ||
                algo.name == 'CreateArrayIterator' ||
                algo.name == 'CreateMapIterator' ||
                algo.name == 'CreateSetIterator'
              ) {
                addMethod(algo.name, algo.length, algo.body);
              } else {
                if (algo.name == 'GetCapabilitiesExecutorFunctions') {
                    algo.body.params = ['resolve', 'reject'];
                    algo.length = 2;
                } else if (algo.name == 'PromiseRejectFunctions') {
                    algo.body.params = ['reason'];
                    algo.length = 1;
                } else if (algo.name == 'PromiseResolveFunctions') {
                    algo.body.params = ['resolution'];
                    algo.length = 1;
                } else if (algo.name == 'AwaitFulfilledFunctions') {
                    algo.body.params = ['value'];
                    algo.length = 1;
                } else if (algo.name == 'AwaitRejectedFunctions') {
                    algo.body.params = ['reason'];
                    algo.length = 1;
                } else if (algo.name =='AsyncGeneratorResumeNextReturnProcessorFulfilledFunctions') {
                    algo.body.params = ['value'];
                    algo.length = 1;
                } else if (algo.name == 'AsyncGeneratorResumeNextReturnProcessorRejectedFunctions') {
                    algo.body.params = ['reason'];
                    algo.length = 1;
                } else if (algo.name == 'Async-from-SyncIteratorValueUnwrapFunctions') {
                    algo.name = 'AsyncfromSyncIteratorValueUnwrapFunctions';
                    algo.body.params = ['value'];
                    algo.length = 1;
                }
                let name = `GLOBAL.${algo.name}`;
                let params = algo.body.params;
                let steps = [];
                for (let i = 0; i < params.length; i++) {
                  let param = params[i];
                  if (!param.startsWith('...')) {
                    steps.push({
                      tokens: ['Let', {'id': param}, 'be', 'GetArgument', '(', {id: 'argumentsList'}, ',', i + '', ')', '.']
                    })
                  }
                }
                function replaceParams (steps, params) {
                    let newSteps = [];
                    for (let i = 0; i < steps.length; i ++) {
                        let item = steps[i];
                        let newStep = undefined;
                        if (item.hasOwnProperty('steps')) {
                            newSteps.push( { steps: replaceParams(item['steps'], params) });
                        } else if (item.hasOwnProperty('tokens')) {
                            newSteps.push({ tokens: replaceParams(item['tokens'], params) });
                        } else if (item.hasOwnProperty('id') && params.indexOf(item['id']) !== -1 && steps[i+1] === "is" && steps[i+2] === "present") {
                            newSteps.push({id: 'argumentsList'});
                            newSteps.push('[');
                            newSteps.push(params.indexOf(item['id']).toString());
                            newSteps.push(']');
                        } else if (item.hasOwnProperty('id') && params.indexOf(item['id']) !== -1 && steps[i+1] === "is" && steps[i+2] === "not" && steps[i+3] === "present") {
                            newSteps.push({id: 'argumentsList'});
                            newSteps.push('[');
                            newSteps.push(params.indexOf(item['id']).toString());
                            newSteps.push(']');
                        } else {
                            newSteps.push(item);
                        }
                    }
                    return newSteps;
                };
                algo.body.steps = replaceParams(algo.body.steps, params);
                algo.body.steps = steps.concat(algo.body.steps);
                algo.body.params = ['this', 'argumentsList', 'NewTarget'];
                addMethod(name, algo.length, algo.body);
              }
              break;
            case 'RuntimeSemantics':
              addMethod(algo.name, algo.length, algo.body);
              break;
            default:
              error(`[NotYetHandle] ${algo.body.kind}`);
          }
        } else {
          error(elem);
        }
      }
    }

    // save ES2020 spec
    let spec = new Spec(globalMethods, consts, grammar, symbols, intrinsics, tys)
    save(zip, spec, 'es2019/spec.json');

    // download files
    download(zip, 'es2019');
  }
});
