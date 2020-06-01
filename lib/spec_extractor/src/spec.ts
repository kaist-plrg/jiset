import { Grammar } from "./grammar";
import { ExtractorRule, TyRule, AlgoRule } from "./rule";
import { HTMLSemanticTag } from "./enum";
import { norm, normName, unwrap, copy } from "./util";
import { AliasMap } from "./types";
import { Algorithm } from "./algorithm";
import assert from "assert";

////////////////////////////////////////////////////////////////////////////////
// Spec Structures
////////////////////////////////////////////////////////////////////////////////
export class Spec {
  algoMap: AlgoMap = {};

  constructor(
    public consts: string[] = [],
    public algorithms: string[] = [],
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

    spec.tys = extractTypes($, rule.tyRule, rule.algoRule, spec);
    spec.algorithms = Object.keys(spec.algoMap);

    return spec;
  }

  // add algorithms
  addAlgorithm(algoRule: AlgoRule, algo: Algorithm) {
    // normalize algorithm names
    let name = algo.head.name;
    name = normName(name);
    for (const from in algoRule.replacePrefix) {
      const to = algoRule.replacePrefix[from];
      name = name.replace(from, to);
    }
    algo.head.name = name;

    // ignore algorithms
    if (name in algoRule.ignores || /[-\[\]@]/g.test(name)) return;

    // replace parameters
    if (name in algoRule.replaceParams) {
      algo.head.params = algoRule.replaceParams[name];
    }

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
    // } else if (name == 'ArrayBindingPattern0BoundNames2') {
    //   let params = data.params;
    //   data.params = ['this'];
    //   addMethod('ArrayBindingPattern0BoundNames0', length, data)
    //   data.params = params;
    // } else if (name == 'ArrayBindingPattern2BoundNames2') {
    //   let params = data.params;
    //   data.params = ['this', 'BindingElementList'];
    //   addMethod('ArrayBindingPattern2BoundNames0', length, data)
    //   data.params = params;
    // } else if (name == 'ArrowParameters0IteratorBindingInitialization0') {
    //   let params = data.params;
    //   data.params = ['BindingIdentifier', '_', 'iteratorRecord', 'environment'];
    //   addMethod('BindingIdentifier0IteratorBindingInitialization0', length, data)
    //   data.params = ['BindingIdentifier', 'iteratorRecord', 'environment'];
    //   addMethod('BindingIdentifier1IteratorBindingInitialization0', length, data)
    //   addMethod('BindingIdentifier2IteratorBindingInitialization0', length, data)
    //   data.params = params;
    // } else if (name == 'CoverParenthesizedExpressionAndArrowParameterList0CoveredFormalsList0') {
    //   addMethod('CoverParenthesizedExpressionAndArrowParameterList1CoveredFormalsList0', length, data);
    // } else if (name == 'AbstractRelationalComparison') {
    //   data.params = ['x', 'y', 'LeftFirst'];
    //   data.steps = [{
    //       'tokens' : ["If", {id: 'LeftFirst'}, "is", "not", "present", ",", "let", {id: 'LeftFirst'}, "be", {value:'true'}, "."]
    //   }].concat(data.steps);
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
    // } else if (name == 'GLOBAL.Array.prototype.push' || name == 'GLOBAL.Array.prototype.concat' || name == 'GLOBAL.Array.prototype.unshift' || name == 'GLOBAL.Number.prototype.toString' || name == 'GLOBAL.String.fromCodePoint' || name == 'GLOBAL.String.prototype.concat' || name == 'GLOBAL.String.fromCharCode') {
    //   length = 1;
    // } else if (name == 'GLOBAL.Object.assign') {
    //   length = 2;
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
    // } else if (name == 'GeneratorYield') {
    //   data.steps[8] = {
    //       tokens: ['ReturnCont', {'id': 'genContext'}, '.', '[', '[', 'ReturnCont', ']', ']', 'to', 'NormalCompletion', '(', {'id': 'iterNextObj'}, ')', '.']
    //   };
    // } else if (name == 'AsyncGeneratorYield') {
    //   data.steps[8] = {
    //       tokens: ['ReturnCont', {'id': 'genContext'}, '.', '[', '[', 'ReturnCont', ']', ']', 'to', '!', 'AsyncGeneratorResolve', '(', {'id': 'generator'}, ',', {'id': 'value'}, ',', {'value': 'false'}, ')', '.']
    //   };
    // } else if (name == 'GeneratorStart') {
    //   data.steps[3].tokens[24].steps[7].tokens[2].steps[1].tokens[0] = 'ReturnCont';
    //   data.steps[3].tokens[24].steps[8].tokens[0] = 'ReturnCont';
    // } else if (name == 'AsyncGeneratorStart') {
    //   data.steps[4].tokens[24].steps[5].tokens[2].steps[1].tokens[13].steps[0].tokens[0] = 'ReturnCont';
    //   data.steps[4].tokens[24].steps[6].tokens[0] = 'ReturnCont';
    // } else if (name == 'AsyncFunctionStart') {
    //   data.steps[2].tokens[24].steps[6].tokens[0] = 'ReturnCont';
    // } else if (name == 'MemberExpression1Evaluation0') {
    //   addMethod('CallExpression3Evaluation0', length, data);
    // } else if (name == 'MemberExpression2Evaluation0') {
    //   addMethod('CallExpression4Evaluation0', length, data);
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
    // } else if (name == 'BuiltinFunctionObject.Call') {
    //   builtinFunctionObjectCall = data;
    // } else if (name == 'BuiltinFunctionObject.Construct') {
    //   let steps = [];
    //   for (let step of builtinFunctionObjectCall.steps) {
    //     steps.push(step);
    //   }
    //   steps[9] = data.steps[0];
    //   data.steps = steps;
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
  tyRule: TyRule,
  algoRule: AlgoRule,
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
        const algo = Algorithm.from($, elem, spec.grammar);
        let name = algo.head.name;
        if (name.startsWith(prefix)) {
          name = unwrap(name, prefix.length);
          algo.head.name = `${tname}.${name}`;
          methods[name] = algo.head.name;
          algo.head.params = [ thisName ].concat(algo.head.params);
        }
        spec.addAlgorithm(algoRule, algo);
      });

    // recursively extract children types
    extractTypes($, children, algoRule, spec, tys, prefix, thisName, methods);
  }

  return tys;
}
