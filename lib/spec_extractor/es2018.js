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
      } else if (name == 'ArrowParameters0IteratorBindingInitialization0') {
        let params = data.params;
        data.params = ['BindingIdentifier', '_', 'iteratorRecord', 'environment'];
        addMethod('BindingIdentifier0IteratorBindingInitialization0', length, data)
        data.params = ['BindingIdentifier', 'iteratorRecord', 'environment'];
        addMethod('BindingIdentifier1IteratorBindingInitialization0', length, data)
        addMethod('BindingIdentifier2IteratorBindingInitialization0', length, data)
        data.params = params;
      } else if (name == 'AbstractRelationalComparison') {
        data.params = ['x', 'y', 'LeftFirst'];
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
      } else if (name == 'ForBodyEvaluation') {
        data.steps[2].tokens[2].steps[2] = {
            tokens: ['If', '!', 'LoopContinues', '(', {id: 'result'}, ',', {id: 'labelSet'}, ')', 'is', {value: 'false'}, ',', 'return', 'Completion', '(', 'UpdateEmpty', '(', {id: 'result'}, ',', {id: 'V'}, ')', ')', '.']
        };
      } else if (name == 'ForInOfBodyEvaluation') {
        let stepIn = data.steps[5].tokens[2].steps;
        let newSteps = stepIn.slice(0, 3);
        newSteps.push({
            tokens: ['Let', {id: 'done'}, 'be', '?', 'IteratorComplete', '(', {id: 'nextResult'}, ')', '.']
        });
        newSteps.push({
            tokens: ['If', {id: 'done'}, 'is', {value: 'true'}, ',', 'return', 'NormalCompletion', '(', {id: 'V'}, ')', '.']
        });
        newSteps = newSteps.concat(stepIn.slice(3));
        data.steps[5].tokens[2] = { steps: newSteps };
      } else if (
        name == 'SubstitutionTemplate0Evaluation0' ||
        name == 'TemplateMiddleList0Evaluation0'
      ) {
        data.steps[2] = {
          tokens: ['Let', {id: 'sub'}, 'be', '?', 'GetValue', '(', {id: 'sub'}, ')', '.']
        };
      } else if (name == 'TemplateMiddleList1Evaluation0') {
        data.steps[2] = {
          tokens: ['Let', {id: 'sub'}, 'be', '?', 'GetValue', '(', {id: 'sub'}, ')', '.']
        };
      } else if (name == 'ClassTail0ClassDefinitionEvaluation3') {
          data.steps[5].tokens[2].steps[1] = {
              tokens: ['Let', {id: 'superclassRef'}, 'be', 'the', 'result', 'of', 'evaluating', 'ClassHeritage', '.']
          };
          data.steps[5].tokens[2].steps[3] = {
              tokens: ['Let', {id: 'superclass'}, 'be', '?', 'GetValue', '(', {id: 'superclassRef'}, ')', '.']
          };
      } else if (name == 'GLOBAL.Array.from') {
        data.steps[7].tokens[7].steps[4].tokens[2].steps[5].tokens[6].steps.length = 2;
      } else if (name == 'GLOBAL.Array.prototype.sort') {
        return;
      } else if (
        name == 'GLOBAL.Function.prototype.toString' ||
        name == 'GLOBAL.Function.prototype.call' ||
        name == 'GLOBAL.Function.prototype.apply'
      ) {
        data.steps = [{
          tokens: ['Let', {id: 'func'}, 'be', 'the', {value: 'this'}, 'value']
        }].concat(data.steps);
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
      } else if (name == 'GLOBAL.Set') {
          data.steps[9].tokens[2].steps[3] = {
              tokens: ['Let', {id: 'status'}, 'be', 'Call', '(', {id: 'adder'}, ',', {id: 'set'}, ',', '«', {id: 'nextValue'}, '»', ')', '.']
          };
      } else if (name == 'GLOBAL.Map' || name == 'GLOBAL.WeakMap') {
         data.steps[9].tokens[2].steps[8] = {
             tokens: ['Let', {id: 'status'}, 'be', 'Call', '(', {id: 'adder'}, ',', {id: 'map'}, ',', '«', {id: 'k'}, ',', {id: 'v'},  '»', ')', '.']
         };
      } else if (name == 'UnaryExpression3Evaluation0') {
        let newSteps = data.steps.slice(0, 1);
        newSteps.push({
          tokens: ['ReturnIfAbrupt', '(', {'id': 'val'}, ')', '.']
        });
        newSteps = newSteps.concat(data.steps.slice(1));
        data.steps = newSteps;
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
      } else if (name == 'MemberExpression1Evaluation0') {
        addMethod('CallExpression3Evaluation0', length, data);
      } else if (name == 'MemberExpression2Evaluation0') {
        addMethod('CallExpression4Evaluation0', length, data);
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
          data.steps[2].tokens[8] = {'value':`"%${errName}Prototype%"`};
          addMethod('GLOBAL.' + errName, length, data);
        }
        return;
      } else if (name == 'GLOBAL.Function') {
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
      data.filename = `es2018/algorithm/${name}.json`;
      save(zip, data, data.filename);
    }
    let globalElementIds = [
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
      'sec-generator-abstract-operations', // 25.4.3
    ];
    for (let id of globalElementIds) {
      for (let elem of getElem(id).getElementsByTagName('emu-alg')) {
        let algo = getAlgo(elem);
        if (algo) {
          if (algo.name == "AbstractEqualityComparison") {
            algo.body.params = ["x", "y"]
          } else if (algo.name == "StrictEqualityComparison") {
            algo.body.params = ["x", "y"]
          }
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
    lexProds = lexProds.concat([
      'prod-NotEscapeSequence',
      'prod-NotCodePoint',
      'prod-CodePoint'
    ].map(getProd));
    let prods = [];
    prods = prods.concat(getSection('sec-expressions'));
    prods = prods.concat(getSection('sec-statements'));
    prods = prods.concat(getSection('sec-functions-and-classes'));
    prods = prods.concat(getSection('sec-scripts-and-modules'));
    prods = prods.concat([
      'prod-AsyncGeneratorMethod',
      'prod-AsyncGeneratorDeclaration',
      'prod-AsyncGeneratorExpression',
      'prod-AsyncGeneratorBody',
      'prod-AssignmentRestProperty',
      'prod-SubstitutionTemplate',
      'prod-BindingRestProperty'
    ].map(getProd));

    // grammar
    grammar = new Grammar(lexProds, prods)

    // Grammar Methods
    ////////////////////////////////////////////////////////////////////////////
    let semMap = {};
    for (let prod of grammar.prods) {
      let lhs = prod.lhs;
      let i = 0;
      for (let rhs of prod.rhsList) {
        let names = [lhs.name + ':'];
        for (let token of rhs.tokens) {
          let newNames = [];
          if (token instanceof Terminal) names.forEach((name) => {
            newNames.push(name + token.term);
          });
          else if (token instanceof Nonterminal) names.forEach((name) => {
            if (token.optional) newNames.push(name);
            newNames.push(name + token.name);
          });
          else if (token instanceof ButNotToken) names.forEach((name) => {
            newNames.push(name + token.base.name + "butnot" + token.cases.map((x) => x.name).join(''));
          });
          else newNames = names;
          names = newNames;
        }
        let j = 0;
        for (name of names) {
          semMap[norm(name)] = { idx: i, subIdx: j };
          j++;
        }
        i++;
      }
    }
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
        let algo = getAlgo(elem);
        if (algo) {
          let p = elem.parentElement.children[1];
          let moreParams = [];
          if (p.tagName == 'P' && p.innerText.startsWith('With parameter')) {
            for (let x of p.getElementsByTagName('var')) {
              moreParams.push(x.innerText)
            }
          }
          let grammar = elem.previousElementSibling;
          if (grammar.tagName == 'EMU-GRAMMAR') {
            let rules = [];
            for (let prod of grammar.getElementsByTagName('emu-production')) {
              for (let rhs of prod.getElementsByTagName('emu-rhs')) {
                let lhsName = prod.getAttribute('name');
                let name = norm(lhsName + ':' + rhs.innerText)
                  .replace(/\[noLineTerminatorhere\]/g, '')
                  .replace(/\[lookahead[^\]]+\]/g, '')
                  .replace(/\[empty\]/g, '');
                if (name in semMap) {
                  let obj = semMap[name];
                  let type = `${lhsName}${obj.idx}`;
                  let algoName = `${algo.name}${obj.subIdx}`;
                  algo.body.params = [];
                  for (let nt of rhs.getElementsByTagName('emu-nt')) {
                    if (nt.innerText !== 'LineTerminator')
                      algo.body.params.push(nt.innerText)
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
                        } else if (item === name) {
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
          let algo = getAlgo(elem);
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
          let algo = getAlgo(elem);
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
      // 'sec-control-abstraction-objects', // 25
      // 'sec-reflection' // 26
    ];
    for (let id of globalObjectMethodIds) {
      for (let elem of getElem(id).getElementsByTagName('emu-alg')) {
        let algo = getAlgo(elem);
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

    // save ES2018 spec
    let spec = new Spec(globalMethods, consts, grammar, symbols, intrinsics, tys)
    save(zip, spec, 'es2018/spec.json');

    // download files
    download(zip, 'es2018');
  }
});
