import { Grammar, Rhs } from "./grammar";
import { ExtractorRule, TyRule, AlgoRule } from "./rule";
import { HTMLSemanticTag, TokenType } from "./enum";
import { getAllAttributes, norm, normName, unwrap, copy } from "./util";
import { AliasMap } from "./types";
import { Algorithm } from "./algorithm";
import assert from "assert";

////////////////////////////////////////////////////////////////////////////////
// Spec Structures
////////////////////////////////////////////////////////////////////////////////
export class Spec {
  algoMap: AlgoMap = {};

  constructor(
    public algorithms: string[] = [],
    public consts: string[] = [],
    public grammar: Grammar = new Grammar(),
    public intrinsics: AliasMap = {},
    public symbols: AliasMap = {},
    public tys: TyMap = {}
  ) { }

  // extract Spec from a ECMAScript html file
  static from(
    $: CheerioStatic,
    rule: ExtractorRule
  ) {
    const spec = new Spec();
    spec.consts = extractConsts($);
    spec.grammar = Grammar.from($, rule);
    spec.intrinsics = extractIntrinsics($, rule.intrinsics.table);
    spec.symbols = extractSymbols($, rule.symbols.table);

    spec.extractGlobalAlgos($, rule);
    spec.extractGrammarAlgos($, rule);
    spec.tys = extractTypes($, rule, rule.tyRule, spec);
    spec.algorithms = Object.keys(spec.algoMap);

    return spec;
  }

  // extract global algorithms
  extractGlobalAlgos(
    $: CheerioStatic,
    rule: ExtractorRule
  ) {
    rule.algoRule.globalElementIds.forEach(id => {
      $(HTMLSemanticTag.ALGO, `#${id}`).each((_, elem) => {
        try {
          const algo = Algorithm.from($, rule, elem, this.grammar);
          this.addAlgorithm(rule.algoRule, algo);
        } catch (_) { }
      });
    })
  }

  // extract grammar algorithms
  extractGrammarAlgos(
    $: CheerioStatic,
    rule: ExtractorRule
  ) {
    rule.algoRule.grammarElementIds.forEach(id => {
      $(HTMLSemanticTag.ALGO, `#${id}`).each((_, elem) => {
        try {
          const algo = Algorithm.from($, rule, elem, this.grammar);
          let grammarElem = $(elem).prev()[0];
          if (grammarElem.name == HTMLSemanticTag.GRAMMAR) {
            this.extractGrammarAlgo($, rule, algo, elem, grammarElem);
          } else {
            this.addAlgorithm(rule.algoRule, algo);
          }
        } catch (_) { }
      });
    })
  }

  // extract grammar algorithm
  extractGrammarAlgo(
    $: CheerioStatic,
    rule: ExtractorRule,
    baseAlgo: Algorithm,
    elem: CheerioElement,
    grammarElem: CheerioElement
  ) {
    const p = $(elem.parent).children()[1];

    // more parameters
    let moreParams: string[] = [];
    if (
      p.name == HTMLSemanticTag.PARAGRAPH &&
      $(p).text().startsWith('With parameter')
    ) {
      $(HTMLSemanticTag.VARIABLE, p).each((_, x) => {
        moreParams.push($(x).text());
      });
    }
    if (baseAlgo.head.name in rule.algoRule.replaceGrammarAlgo) {
      const info = rule.algoRule.replaceGrammarAlgo[baseAlgo.head.name];
      baseAlgo.head.name = info.name;
      moreParams = moreParams.concat(info.moreParams);
    }

    const idxMap = this.grammar.idxMap;
    const rules = [];
    $(HTMLSemanticTag.PRODUCTION, grammarElem).each((_, prod) => {
      $(HTMLSemanticTag.RHS, prod).each((_, rhsElem) => {
        // copy algorithm
        const algo = copy(baseAlgo);

        // index map check
        const lhsName = getAllAttributes(prod).name;
        const rhs = Rhs.from($, rule, rhsElem);
        const name = rhs.getCaseName(lhsName);
        if (!(name in idxMap)) return; // TODO

        // algorithm name
        const obj = idxMap[name];
        const ty = `${lhsName}${obj.idx}`;
        const algoName = `${algo.head.name}${obj.subIdx}`;
        algo.head.name = ty + algoName;

        // algorithm parameters
        const params: string[] = [];
        for (const token of rhs.tokens) {
          if (
            token.ty === TokenType.NON_TERMINAL &&
            token.name !== "LineTerminator"
          ) params.push(token.name);
        }
        algo.head.params = [ "this" ].concat(params).concat(moreParams);

        // replace steps for `this` values
        if (algo.head.params.indexOf(lhsName) === -1) {
          algo.steps = replaceStepForThis(algo.steps, lhsName);
        }

        // add algorithm
        this.addAlgorithm(rule.algoRule, algo);
      });
    });
  }

  // add algorithms
  addAlgorithm(algoRule: AlgoRule, algo: Algorithm) {
    // modify names
    let name = algo.head.name;
    name = normName(name);
    for (const from in algoRule.replacePrefix) {
      const to = algoRule.replacePrefix[from];
      name = name.replace(from, to);
    }
    algo.head.name = name;

    // modify parameters
    let params = algo.head.params;
    if (name in algoRule.replaceParams) {
      params = algoRule.replaceParams[name];
    }
    algo.head.params = params;

    // modify steps
    let steps = algo.steps;
    if (name in algoRule.preSteps) {
      steps = algoRule.preSteps[name].concat(steps);
    }
    if (name in algoRule.replaceSteps) {
      for (const stepInfo of algoRule.replaceSteps[name]) {
        replaceStep(algo, "steps", stepInfo.idxList, stepInfo.item);
      }
    }
    algo.steps = steps;

    // modify lengths
    let length = algo.head.length;
    if (name in algoRule.replaceLength) {
      length = algoRule.replaceLength[name];
    }
    algo.head.length = length;

    // forward algorithms
    if (name in algoRule.forwards) {
      for (const newName of algoRule.forwards[name]) {
        const newAlgo = copy(algo);
        newAlgo.head.name = newName;
        this.addAlgorithm(algoRule, newAlgo);
      }
    }

    // ignore algorithms
    if (name in algoRule.ignores || /[-\[\]@]/g.test(name)) return;

    // TODO
    // if (name == 'CreateIntrinsics') {
    //   data.steps[2] = { 'tokens' : ['Set', {id: 'intrinsics'}, '.', '[', '[', '%', 'ObjectPrototype', '%', ']', ']', 'to', '%', 'ObjectPrototype', '%', '.'] };
    //   data.steps[3] = { 'tokens' : ['Set', {id: 'intrinsics'}, '.', '[', '[', '%', 'ThrowTypeError', '%', ']', ']', 'to', '%', 'ThrowTypeError', '%', '.'] };
    //   data.steps[4] = { 'tokens' : ['Set', {id: 'intrinsics'}, '.', '[', '[', '%', 'FunctionPrototype', '%', ']', ']', 'to', '%', 'FunctionPrototype', '%', '.'] };
    //   data.steps[5] = data.steps[12];
    //   data.steps[6] = data.steps[11];
    //   data.steps[6].tokens[3] = ['%', 'FunctionPrototype', '%'];
    //   data.steps[6].tokens = data.steps[6].tokens.flat();
    //   data.steps[7] = data.steps[13];
    //   data.steps.length = 8;
    // } else if (name == 'Evaluation') {
    //   data.params = ['this', 'A', 'B'];
    //   data.steps[6] = {
    //     tokens: ['Return', {id: 'lnum'}, '', {id: 'rnum'}, '.']
    //   };
    //   data.steps[6].tokens[2] = '&';
    //   addMethod('BitwiseANDExpression1Evaluation0', length, data);
    //   data.steps[6].tokens[2] = '^';
    //   addMethod('BitwiseXORExpression1Evaluation0', length, data);
    //   data.steps[6].tokens[2] = '|';
    //   addMethod('BitwiseORExpression1Evaluation0', length, data);
    //   return;
    // } else if (name == 'GLOBAL.Array.from') {
    //   data.steps[7].tokens[7].steps[4].tokens[2].steps[5].tokens[6].steps.length = 2;
    // } else if (name == 'GLOBAL.Array') {
    //   if (arrayCount == 0) {
    //     arrayData = data;
    //     arrayData.steps = [{
    //       tokens: ['If', 'the', 'length', 'of', {id: 'argumentsList'}, '=', {value:'0'}, ',', {steps: data.steps}]
    //     }];
    //     arrayCount++;
    //     return;
    //   } else if (arrayCount == 1) {
    //     arrayData.steps = arrayData.steps.concat([{
    //       tokens: ['Else', 'if', 'the', 'length', 'of', {id: 'argumentsList'}, '=', {value:'1'}, ',', {steps: data.steps}]
    //     }]);
    //     arrayCount++;
    //     return;
    //   } else {
    //     arrayData.steps = arrayData.steps.concat([{
    //       tokens: ['Else', ',', {steps: data.steps}]
    //     }]);
    //     data = arrayData;
    //   }
    // } else if (name == 'UnaryExpression3Evaluation0') { // specError #15
    //   let newSteps = data.steps.slice(0, 1);
    //   newSteps.push({
    //     tokens: ['ReturnIfAbrupt', '(', {'id': 'val'}, ')', '.']
    //   });
    //   newSteps = newSteps.concat(data.steps.slice(1));
    //   data.steps = newSteps;
    // } else if (name == 'CallExpression0CoveredCallExpression0') {
    //   data.steps[0].tokens[7] = 'this';
    //   addMethod('CoverCallExpressionAndAsyncArrowHead0CoveredCallExpression0', length, data);
    //   return;
    // } else if (name == 'CallExpression0Evaluation0') { // specError #15?
    //   let newSteps = data.steps.slice(0, 4);
    //   newSteps.push({
    //     tokens: ['ReturnIfAbrupt', '(', {'id': 'ref'}, ')', '.']
    //   });
    //   newSteps = newSteps.concat(data.steps.slice(4));
    //   data.steps = newSteps;
    // } else if (name == 'EvaluateCall') { // specError #15
    //   data.steps = [{
    //     tokens: ['ReturnIfAbrupt', '(', {'id': 'ref'}, ')', '.']
    //   }].concat(data.steps);
    // } else if (name == 'ClassTail0ClassDefinitionEvaluation3') {
    //   let newSteps = data.steps.slice(0, 12);
    //   newSteps.push({
    //     tokens: ['ReturnIfAbrupt', '(', {'id': 'constructorInfo'}, ')', '.']
    //   });
    //   newSteps = newSteps.concat(data.steps.slice(12));
    //   data.steps = newSteps;
    // } else if (methodSet.has(name)) switch (name) {
    //   case 'MakeArgGetter':
    //     name = 'ArgGetter';
    //     data.params = ['_', '_', '_', 'f'];
    //     break;
    //   case 'MakeArgSetter':
    //     name = 'ArgSetter';
    //     data.params = ['_', 'argumentsList', '_', 'f'];
    //     data.steps = [{
    //       tokens: ['Let', {id: 'value'}, 'be', {id: 'argumentsList'}, '[', '0', ']']
    //     }].concat(data.steps);
    //     break;
    //   case 'Await':
    //     data.params = ['value'];
    //     data.steps[11].tokens[0] = 'ReturnCont';
    //     break;
    //   default:
    //     error(`[AlreadyExist] ${name}`);
    //     return;
    // } else if (name == 'GLOBAL.NativeError') {
    //   let errList =  [
    //     'EvalError',
    //     'RangeError',
    //     'ReferenceError',
    //     'SyntaxError',
    //     'TypeError',
    //     'URIError',
    //   ];
    //   for (let errName of errList) {
    //     data.steps[2].tokens[8] = {'code':`"%${errName}Prototype%"`};
    //     addMethod('GLOBAL.' + errName, length, data);
    //   }
    //   return;
    // } else if (name == 'GLOBAL.Function' || name == 'GLOBAL.GeneratorFunction' || name == 'GLOBAL.AsyncFunction' || name == 'GLOBAL.AsyncGeneratorFunction') {
    //   data.steps = data.steps.slice(5)
    // }

    // handle already existed algorithms
    if (name in this.algoMap) return;

    this.algoMap[name] = algo;
  }

  // serialization
  serialize() {
    this.grammar.serialize();
    delete this.algoMap;
  }
}

// type map
export interface TyMap {
  [ attr: string ]: AliasMap
}

// algorithm map
export interface AlgoMap {
  [ attr: string ]: Algorithm
}

////////////////////////////////////////////////////////////////////////////////
// Helper Functions
////////////////////////////////////////////////////////////////////////////////
// extract constants
export const extractConsts = (
  $: CheerioStatic
): string[] => {
  // set of constant names
  let cset = new Set<string>();

  // extract constants
  $(HTMLSemanticTag.CONST)
    .each((_, constElem) => {
      const name = $(constElem).text();
      cset.add(name.replace(/-/g, "")); // TODO do not remove `-` in constant names
    });

  // return list of constants
  return Array.from(cset);
}

// extract intrinsics
export const extractIntrinsics = (
  $: CheerioStatic,
  tableId: string
): AliasMap => {
  // alias map for intrinsics
  const intrinsics: AliasMap = {};

  // extract intrinsics
  $(HTMLSemanticTag.TABLE_ROW, `${HTMLSemanticTag.TABLE}#${tableId}`)
    .each((_, tr) => {
      let children = $(tr).children();
      if (children[0].name === HTMLSemanticTag.TABLE_HEAD) return;
      let intrinsic = norm($(children[0]).text());
      let property = norm($(children[1]).text());
      if (property == '') property = `INTRINSIC_${unwrap(intrinsic)}`;
      intrinsics[intrinsic] = `GLOBAL.${property}`; // TODO handle it in JISET
    });
  return intrinsics;
}

// extract symbols
export const extractSymbols = (
  $: CheerioStatic,
  tableId: string
): AliasMap => {
  // alias map for symbols
  const symbols: AliasMap = {};

  // extract symbols
  $(HTMLSemanticTag.TABLE_ROW, `${HTMLSemanticTag.TABLE}#${tableId}`)
    .each((_, tr) => {
      let children = $(tr).children();
      if (children[0].name === HTMLSemanticTag.TABLE_HEAD) return;
      let sym = norm($(children[0]).text());
      let desc = norm($(children[1]).text());
      symbols[sym] = unwrap(desc);
    });
  return symbols;
}

// extract types
export const extractTypes = (
  $: CheerioStatic,
  rule: ExtractorRule,
  tyRule: TyRule,
  spec: Spec,
  tys: TyMap = {},
  basePrefix: string = "",
  baseThisName: string = "this",
  baseMethods: AliasMap = {}
) => {
  for (const tname in tyRule) {
    // initialization
    const methods: AliasMap = copy(baseMethods);
    tys[tname] = methods;
    let info = tyRule[tname];
    if (typeof info === "string") info = { id: info };
    if (!info.prefix) info.prefix = basePrefix;
    if (!info.thisName) info.thisName = baseThisName;
    if (!info.children) info.children = {};
    const { id, prefix, thisName, children } = info;

    // extract algorithms and link member methods
    $(HTMLSemanticTag.ALGO, `${HTMLSemanticTag.CLAUSE}#${id}`)
      .each((_, elem) => {
        try {
          const algo = Algorithm.from($, rule, elem, spec.grammar);
          let name = algo.head.name;
          if (name.startsWith(prefix)) {
            name = unwrap(name, prefix.length);
            algo.head.name = `${tname}.${name}`;
            methods[name] = algo.head.name;
            algo.head.params = [ thisName ].concat(algo.head.params);
          }
          spec.addAlgorithm(rule.algoRule, algo);
        } catch (_) { }
      });

    // recursively extract children types
    extractTypes($, rule, children, spec, tys, prefix, thisName, methods);
  }

  return tys;
}

// replace steps for `this` values
export const replaceStepForThis = (
  items: any[],
  name: string
): any[] => {
  return items.map(item => {
    if (item.steps) {
      return { steps: replaceStepForThis(item["steps"], name) };
    } else if (item.tokens) {
      return { tokens: replaceStepForThis(item["tokens"], name) };
    } else if (item.nt === name) {
      return "this";
    } else {
      return item;
    }
  });
};

// replace steps
export const replaceStep = (
  obj: any,
  prop: string,
  idxList: any[],
  item: any
) => {
  if (idxList.length == 0) { obj[prop] = item; return; }
  const nextObj = obj[prop][idxList[0]];
  switch (prop) {
    case "steps": replaceStep(nextObj, "tokens", idxList.slice(1), item); return;
    case "tokens": replaceStep(nextObj, "steps", idxList.slice(1), item); return;
  }
};
