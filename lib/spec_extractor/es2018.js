loadSpec(() => {
  // JSZip setting
  zip = new JSZip();

  /******************** Global Methods ********************/
  let globalMethods = [];
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
    'sec-agents' // 8.7
  ];
  for (let id of globalElementIds) {
    for (let elem of getElem(id).getElementsByTagName('emu-alg')) {
      let algo = getAlgo(elem);
      if (algo) {
        globalMethods.push(algo.name);
        save(algo.code, `es2018/algorithm/${algo.name}.algorithm`);
      } else {
        console.error(elem);
      }
    }
  }

  /******************** Grammar ********************/
  // get elements
  function getElem(id) { return document.getElementById(id) }

  // get productions from sections
  function getSection(name) {
    return Array.from(getElem(name).children)
      .filter(child => child.tagName == Prod.tag)
      .map(Prod.from)
  }

  // get production from an element
  function getProd(name) {
    return Prod.from(getElem(name));
  }

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
  let grammar = new Grammar(lexProds, prods)

  /******************** Grammar ********************/
  let tys = [];
  let tyMap = {
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
  for (let tname in tyMap) {
    let methods = [];
    for (let id of tyMap[tname]) {
      for (let elem of document.getElementById(id).getElementsByTagName('emu-alg')) {
        let algo = getAlgo(elem);
        if (algo) {
          let name = `${tname}.${algo.name}`
          methods.push(algo.name);
          let lines = algo.code.split('\n');
          let code = [lines[0], '  <param>this</param>'].concat(lines.slice(1)).join('\n');
          save(code, `es2018/algorithm/${name}.algorithm`);
        } else {
          console.error(elem);
        }
      }
    }
    tys.push({
      name: tname,
      methods: methods
    });
  }

  // save ES2018 spec
  let spec = new Spec(globalMethods, grammar, tys)
  save(JSON.stringify(spec, null, 2), 'es2018/spec.json');

  // download files
  download('es2018');
});
