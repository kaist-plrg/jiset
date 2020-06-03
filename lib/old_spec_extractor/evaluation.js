SpecExtractor.loadSpec(() => {
  with(SpecExtractor) {
    // JSZip setting
    let zip = new JSZip();

    // Spec components
    let methods = {};
    let grammar;

    // add methods
    function addMethod(name, data) {
      data.length = 0;
      data.filename = `dummy.json`;
      let idx;
      if (methods[name] === undefined) methods[name] = 0;
      name += methods[name]++;
      console.log(name, data);
      data.filename = `es2000/algorithm/${name}.json`;
      save(zip, data, data.filename);
    }

    // simple normalization
    function simpleNorm(str) {
      return str.replace(/[^a-zA-Z0-9.:(),]/g,'');
    }

    // get dummy algorithms
    function getDummyAlgo(elem, grammar, kind) {
      try {
        let body = {
          kind: "Method",
          lang: kind == "Language",
          params: [],
          steps: getSteps(elem.children[0], grammar)
        };
        let clauseElem = elem;
        while (clauseElem.tagName != 'EMU-CLAUSE') {
          clauseElem = clauseElem.parentElement;
        }
        let name = simpleNorm(clauseElem.children[0].innerText);
        return {
          name: name,
          body: body
        }
      } catch (e) {
        error(`${e}: ${elem}`);
      }
    }

    // Global Methods
    ////////////////////////////////////////////////////////////////////////////
    let globalElementIds = [
      'sec-ecmascript-data-types-and-values', // 6
      'sec-abstract-operations', //7
      'sec-executable-code-and-execution-contexts', // 8
      'sec-ordinary-and-exotic-objects-behaviours' // 9
    ];
    for (let id of globalElementIds) {
      for (let elem of getElem(id).getElementsByTagName('emu-alg')) {
        let algo = getDummyAlgo(elem, null, 'Language');
        addMethod(algo.name, algo.body);
      }
    }

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

    // grammar
    grammar = new Grammar(lexProds, prods)

    // Grammar Methods
    ////////////////////////////////////////////////////////////////////////////
    let grammarSections = [
      'sec-ecmascript-language-expressions', // 12
      'sec-ecmascript-language-statements-and-declarations', // 13
      'sec-ecmascript-language-functions-and-classes', // 14
      'sec-ecmascript-language-scripts-and-modules' // 15
    ];
    for (let id of grammarSections) {
      let section = document.getElementById(id);
      for (let elem of section.getElementsByTagName('emu-alg')) {
        let algo = getDummyAlgo(elem, grammar, 'Language');
        if (algo) {
          let p = elem.parentElement.children[1];
          let grammarElem = elem.previousElementSibling;
          if (grammarElem.tagName == 'EMU-GRAMMAR') {
            let rules = [];
            for (let prod of grammarElem.getElementsByTagName('emu-production')) {
              for (let rhs of prod.getElementsByTagName('emu-rhs')) {
                let lhsName = prod.getAttribute('name');
                let name = simpleNorm(lhsName + ':' + rhs.innerText)
                addMethod(algo.name + name, algo.body);
              }
            }
          } else {
            addMethod(algo.name, algo.body);
          }
        }
      }
    }

    // Builtin Objects
    ////////////////////////////////////////////////////////////////////////////
    // let globalObjectMethodIds = [
    //   'sec-global-object', // 18
    //   'sec-object-objects', // 19.1
    //   'sec-function-objects', // 19.2
    //   'sec-boolean-objects', // 19.3
    //   'sec-symbol-objects', // 19.4
    //   'sec-error-objects', // 19.5
    //   'sec-number-objects', // 20.1
    //   'sec-string-objects', // 21.1
    //   'sec-array-objects', // 22.1
    //   'sec-map-objects', // 23.1
    //   'sec-set-objects', // 23.2
    //   'sec-weakmap-objects', // 23.3
    //   'sec-weakset-objects', // 23.4
    //   'sec-control-abstraction-objects', // 25
    // ];
    let globalObjectMethodIds = [
      'sec-global-object', // 18
      'sec-fundamental-objects', // 19
      'sec-numbers-and-dates', // 20
      'sec-text-processing', // 21
      'sec-indexed-collections', // 22
      'sec-keyed-collection', // 23
      'sec-structured-data', // 24
      'sec-control-abstraction-objects', // 25
      'sec-reflection', // 26
      'sec-memory-model', // 27
    ];
    for (let id of globalObjectMethodIds) {
      if (getElem(id)) {
        for (let elem of getElem(id).getElementsByTagName('emu-alg')) {
          let algo = getDummyAlgo(elem, grammar, 'Builtin');
          addMethod(algo.name, algo.body);
        }
      }
    }

    // save ES2000 spec
    let spec = new Spec([], [], grammar, {}, {}, {})
    save(zip, spec, 'es2000/spec.json');

    // download files
    download(zip, 'es2000');
  }
});
