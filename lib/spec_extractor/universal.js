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
      if (/[-\[\]@]/g.test(name)) {
        error(`[NotYetHandle] ${name}`);
        return;
      } else if (methodSet.has(name)) switch (name) {
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
      }
      methodSet.add(name);
      globalMethods.push(name);
      data.length = length;
      data.filename = `es2000/algorithm/${name}.json`;
      save(zip, data, data.filename);
    }
    let globalElementIds = [
      'sec-ecmascript-data-types-and-values', // 6
      'sec-abstract-operations', //7
      'sec-executable-code-and-execution-contexts', // 8
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
              for (let rhs of prod.getElementsByTagName('emu-rhs')) {
                let lhsName = prod.getAttribute('name');
                let name = norm(lhsName + ':' + rhs.innerText)
                  .replace(/\[noLineTerminatorhere\]/g, '')
                  .replace(/\[lookahead[^\]]+\]/g, '')
                  .replace(/\[empty\]/g, '');
                if (name in grammar.idxMap) {
                  let obj = grammar.idxMap[name];
                  let type = `${lhsName}${obj.idx}`;
                  let algoName = `${algo.name}${obj.subIdx}`;
                  algo.body.params = [];
                  for (let nt of rhs.getElementsByTagName('emu-nt')) {
                    if (nt.innerText !== 'LineTerminator')
                      algo.body.params.push(nt.innerText)
                  }
                  algo.body.params = [ 'this' ].concat(algo.body.params);
                  algo.body.params = algo.body.params.concat(moreParams);
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
      if (getElem(id))
      for (let elem of getElem(id).getElementsByTagName('emu-alg')) {
        let algo = getAlgo(elem, grammar);
        if (algo) {
          if (algo.name.startsWith('Propertiesof')) {
              try {
                let prev = elem.previousElementSibling;
                algo.name = prev.getElementsByTagName('dfn')[0].innerText;
                algo.body.params = ['value'];
                addMethod(algo.name, algo.length, algo.body);
              } catch(e) {
              }
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

    // save ES2000 spec
    let spec = new Spec(globalMethods, consts, grammar, symbols, intrinsics, tys)
    save(zip, spec, 'es2000/spec.json');

    // download files
    download(zip, 'es2000');
  }
});
