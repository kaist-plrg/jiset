import { Grammar, Rhs } from "./grammar";
import { ExtractorRule, TyRule, AlgoRule } from "./rule";
import { HTMLSemanticTag, TokenType, AlgoKind } from "./enum";
import { getAllAttributes, norm, simpleNorm, normName, unwrap, copy } from "./util";
import { map } from "./types";
import { Algorithm, Step } from "./algorithm";
import assert from "assert";

////////////////////////////////////////////////////////////////////////////////
// Spec Structures
////////////////////////////////////////////////////////////////////////////////
export class Spec {
  algoMap: map<Algorithm> = {};
  idxMap: map<number> = {};

  constructor(
    public globalMethods: string[] = [],
    public consts: string[] = [],
    public grammar: Grammar = new Grammar(),
    public intrinsics: map<string> = {},
    public symbols: map<string> = {},
    public tys: TyMap = {}
  ) { }

  // extract Spec from a ECMAScript html file
  static from(
    $: CheerioStatic,
    rule: ExtractorRule,
    forEval: boolean
  ) {
    const spec = new Spec();
    if (forEval) {
      spec.grammar = Grammar.from($, rule, true);
      spec.extractGlobalAlgos($, rule, true);
      spec.extractGrammarAlgos($, rule, true);
      spec.extractBuiltinAlgos($, rule, true);
    } else {
      spec.consts = extractConsts($);
      spec.grammar = Grammar.from($, rule);
      spec.intrinsics = extractIntrinsics($, rule.intrinsics.table);
      spec.symbols = extractSymbols($, rule.symbols.table);

      spec.handleOmittedScriptSemantics(rule.algoRule);
      spec.extractGlobalAlgos($, rule);
      spec.extractGrammarAlgos($, rule);
      spec.extractBuiltinAlgos($, rule);
      spec.tys = extractTypes($, rule, rule.tyRule, spec);
      spec.globalMethods = Object.keys(spec.algoMap);
    }
    return spec;
  }

  // extract global algorithms
  extractGlobalAlgos(
    $: CheerioStatic,
    rule: ExtractorRule,
    forEval: boolean = false
  ) {
    if (forEval) {
      const { globalElementIds } = rule.algoRule;
      for (const id of globalElementIds) {
        $(HTMLSemanticTag.ALGO, `#${id}`).each((_, elem) => {
          try {
            const algo = Algorithm.getDummy($, rule, elem, this.grammar, true);
            this.addAlgorithm(rule.algoRule, algo, true);
          } catch (_) { }
        });
      }
    } else rule.algoRule.globalElementIds.forEach(id => {
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
    rule: ExtractorRule,
    forEval: boolean = false
  ) {
    if (forEval) {
      const { grammarElementIds } = rule.algoRule;
      for (const id of grammarElementIds) {
        $(HTMLSemanticTag.ALGO, `#${id}`).each((_, elem) => {
          try {
            const algo = Algorithm.getDummy($, rule, elem, this.grammar, true);
            const grammarElem = $(elem).prev()[0];
            if (grammarElem.name == HTMLSemanticTag.GRAMMAR) {
              const rules = [];
              $(HTMLSemanticTag.PRODUCTION, grammarElem).each((_, prod) => {
                const { collapsed } = getAllAttributes(prod);
                $(HTMLSemanticTag.RHS, prod).each((_, rhs) => {
                  $(HTMLSemanticTag.MODS, rhs).each((_, mods) => {
                    if(collapsed === "") $(mods).remove();
                  });
                  const lhsName = getAllAttributes(prod).name;
                  const name = simpleNorm(lhsName + ':' + $(rhs).text());
                  const newAlgo = copy(algo);
                  newAlgo.head.name += name;
                  this.addAlgorithm(rule.algoRule, newAlgo, true);
                });
              });
            } else {
              this.addAlgorithm(rule.algoRule, algo, true);
            }
          } catch (_) { }
        });
      }
    } else rule.algoRule.grammarElementIds.forEach(id => {
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
    });
  }

  // extract Builtin algorithms
  extractBuiltinAlgos(
    $: CheerioStatic,
    rule: ExtractorRule,
    forEval: boolean = false
  ) {
    if (forEval) {
      const { builtinElementIds } = rule.algoRule;
      for (const id of builtinElementIds) {
        $(HTMLSemanticTag.ALGO, `#${id}`).each((_, elem) => {
          try {
            const algo = Algorithm.getDummy($, rule, elem, this.grammar, false);
            this.addAlgorithm(rule.algoRule, algo, true);
          } catch (e) { }
        });
      }
      return;
    }
    rule.algoRule.builtinElementIds.forEach(id => {
      $(HTMLSemanticTag.ALGO, `#${id}`).each((_, elem) => {
        try {
          const algo = Algorithm.from($, rule, elem, this.grammar);
          const { name, kind } = algo.head;
          algo.head.lang = false;

          // TODO refactoring
          if (name.startsWith('Propertiesof')) {
            let prev = $(elem).prev();
            algo.head.name = $("dfn", prev).text();
            algo.head.params = ["value"];
          } else outer: switch (kind) {
            case AlgoKind.METHOD: {
              switch (name) {
                case "CreateStringIterator":
                case "CreateArrayIterator":
                case "CreateMapIterator":
                case "CreateSetIterator":
                  break outer;
                case "GetCapabilitiesExecutorFunctions":
                  algo.head.params = ["resolve", "reject"];
                  algo.head.length = 2;
                  break;
                case "PromiseRejectFunctions":
                  algo.head.params = ["reason"];
                  algo.head.length = 1;
                  break;
                case "PromiseResolveFunctions":
                  algo.head.params = ["resolution"];
                  algo.head.length = 1;
                  break;
                case "AwaitFulfilledFunctions":
                  algo.head.params = ["value"];
                  algo.head.length = 1;
                  break;
                case "AwaitRejectedFunctions":
                  algo.head.params = ["reason"];
                  algo.head.length = 1;
                  break;
                case "AsyncGeneratorResumeNextReturnProcessorFulfilledFunctions":
                  algo.head.params = ["value"];
                  algo.head.length = 1;
                  break;
                case "AsyncGeneratorResumeNextReturnProcessorRejectedFunctions":
                  algo.head.params = ["reason"];
                  algo.head.length = 1;
                  break;
                case "Async-from-SyncIteratorValueUnwrapFunctions":
                  algo.head.name = "AsyncfromSyncIteratorValueUnwrapFunctions";
                  algo.head.params = ["value"];
                  algo.head.length = 1;
                  break;
              }
              algo.head.name = `GLOBAL.${algo.head.name}`;
              const params = algo.head.params;
              const steps: Step[] = [];
              for (let i = 0; i < params.length; i++) {
                let param = params[i];
                if (!param.startsWith('...')) {
                  steps.push({
                    tokens: ['Let', {'id': param}, 'be', 'GetArgument', '(', {id: 'argumentsList'}, ',', i + '', ')', '.']
                  })
                }
              }
              algo.steps = replaceParams(algo.steps, params);
              algo.steps = steps.concat(algo.steps);
              algo.head.params = ['this', 'argumentsList', 'NewTarget'];
              break;
            }
            case AlgoKind.RUNTIME:
              break;
            default:
              throw new Error(`[NotYetHandle] ${kind}`);
          }

          this.addAlgorithm(rule.algoRule, algo);
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
  addAlgorithm(
    algoRule: AlgoRule,
    algo: Algorithm,
    forEval: boolean = false
  ) {
    if (forEval) {
      algo.head.length = 0;
      const idxMap = this.idxMap;
      const name = algo.head.name;
      if (idxMap[name] === undefined) idxMap[name] = 0;
      algo.head.name += idxMap[name]++;
      this.algoMap[algo.head.name] = algo;
      return;
    }

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
    if (algoRule.ignores.indexOf(name) != -1 || /[-\[\]@]/g.test(name)) return;

    // TODO refactoring
    if (name == "CreateIntrinsics") {
      algo.steps[2] = { "tokens" : ["Set", {id: "intrinsics"}, ".", "[", "[", "%", "ObjectPrototype", "%", "]", "]", "to", "%", "ObjectPrototype", "%", "."] };
      algo.steps[3] = { "tokens" : ["Set", {id: "intrinsics"}, ".", "[", "[", "%", "ThrowTypeError", "%", "]", "]", "to", "%", "ThrowTypeError", "%", "."] };
      algo.steps[4] = { "tokens" : ["Set", {id: "intrinsics"}, ".", "[", "[", "%", "FunctionPrototype", "%", "]", "]", "to", "%", "FunctionPrototype", "%", "."] };
      algo.steps[5] = algo.steps[12];
      algo.steps[6].tokens = ["Perform","AddRestrictedFunctionProperties","(","%","FunctionPrototype","%",",",{"id":"realmRec"},")","."];
      algo.steps[7] = algo.steps[13];
      algo.steps.length = 8;
    } else if (name == "Evaluation") {
      algo.head.params = ["this", "A", "B"];
      algo.steps[6] = {
        tokens: ["Return", {id: "lnum"}, "", {id: "rnum"}, "."]
      };

      algo.steps[6].tokens[2] = "&";
      algo.head.name = "BitwiseANDExpression1Evaluation0";
      this.addAlgorithm(algoRule, copy(algo));

      algo.steps[6].tokens[2] = "^";
      algo.head.name = "BitwiseXORExpression1Evaluation0";
      this.addAlgorithm(algoRule, copy(algo));

      algo.steps[6].tokens[2] = "|";
      algo.head.name = "BitwiseORExpression1Evaluation0";
      this.addAlgorithm(algoRule, copy(algo));

      return;
    } else if (name == "GLOBAL.Array.from") {
      const item: any = algo;
      item.steps[7].tokens[7].steps[4].tokens[2].steps[5].tokens[6].steps.length = 2;
    } else if (name == "GLOBAL.Array") {
      if (algoRule.arrayCount == 0) {
        algoRule.arrayAlgo = algo;
        algoRule.arrayAlgo.steps = [{
          tokens: ["If", "the", "length", "of", {id: "argumentsList"}, "=", {value:"0"}, ",", {steps: algo.steps}]
        }];
        algoRule.arrayCount++;
        return;
      } else if (algoRule.arrayCount == 1) {
        algoRule.arrayAlgo.steps = algoRule.arrayAlgo.steps.concat([{
          tokens: ["Else", "if", "the", "length", "of", {id: "argumentsList"}, "=", {value:"1"}, ",", {steps: algo.steps}]
        }]);
        algoRule.arrayCount++;
        return;
      } else {
        algoRule.arrayAlgo.steps = algoRule.arrayAlgo.steps.concat([{
          tokens: ["Else", ",", {steps: algo.steps}]
        }]);
        algo = algoRule.arrayAlgo;
      }
    } else if (name == "UnaryExpression3Evaluation0") { // specError #15
      let newSteps = algo.steps.slice(0, 1);
      newSteps.push({
        tokens: ["ReturnIfAbrupt", "(", {"id": "val"}, ")", "."]
      });
      newSteps = newSteps.concat(algo.steps.slice(1));
      algo.steps = newSteps;
    } else if (name == "CallExpression2HasCallInTailPosition0") {
      algo.steps[0].tokens[2] = "this";
    } else if (name == "CallExpression5HasCallInTailPosition0") {
      algo.steps[0].tokens[2] = "this";
    } else if (name == "CallExpression0CoveredCallExpression0") {
      algo.steps[0].tokens[7] = "this";
      name = "CoverCallExpressionAndAsyncArrowHead0CoveredCallExpression0";
      algo.head.name = name;
    } else if (name == "CallExpression0Evaluation0") { // specError #15?
      let newSteps = algo.steps.slice(0, 4);
      newSteps.push({
        tokens: ["ReturnIfAbrupt", "(", {"id": "ref"}, ")", "."]
      });
      newSteps = newSteps.concat(algo.steps.slice(4));
      algo.steps = newSteps;
    } else if (name == "EvaluateCall") { // specError #15
      const preSteps: Step[] = [{
        tokens: ["ReturnIfAbrupt", "(", {"id": "ref"}, ")", "."]
      }];
      algo.steps = preSteps.concat(algo.steps);
    } else if (name == "ClassTail0ClassDefinitionEvaluation3") {
      let newSteps = algo.steps.slice(0, 12);
      newSteps.push({
        tokens: ["ReturnIfAbrupt", "(", {"id": "constructorInfo"}, ")", "."]
      });
      newSteps = newSteps.concat(algo.steps.slice(12));
      algo.steps = newSteps;

    // handle already existed algorithms
    } else if (name in this.algoMap) switch (name) {
      case "MakeArgGetter":
        name = "ArgGetter";
        algo.head.params = ["_", "_", "_", "f"];
        break;
      case "MakeArgSetter":
        name = "ArgSetter";
        algo.head.params = ["_", "argumentsList", "_", "f"];
        const preSteps: Step[] = [{
          tokens: ["Let", {id: "value"}, "be", {id: "argumentsList"}, "[", "0", "]"]
        }];
        algo.steps = preSteps.concat(algo.steps);
        break;
      case "Await":
        algo.head.params = ["value"];
        algo.steps[11].tokens[0] = "ReturnCont";
        break;
      default:
        // TODO throw new Error(`[AlreadyExist] ${name}`);
        return;
    } else if (name == "GLOBAL.NativeError") {
      let errList =  [
        "EvalError",
        "RangeError",
        "ReferenceError",
        "SyntaxError",
        "TypeError",
        "URIError",
      ];
      for (let errName of errList) {
        algo.steps[2].tokens[8] = {"code":`"%${errName}Prototype%"`};
        algo.head.name = "GLOBAL." + errName;
        this.addAlgorithm(algoRule, copy(algo));
      }
      return;
    } else if (name == "GLOBAL.Function" || name == "GLOBAL.GeneratorFunction" || name == "GLOBAL.AsyncFunction" || name == "GLOBAL.AsyncGeneratorFunction") {
      algo.steps = algo.steps.slice(5)
    }

    this.algoMap[name] = algo;
  }

  // serialization
  serialize() {
    this.grammar.serialize();
    delete this.algoMap;
    delete this.idxMap;
  }

  // TODO refactoring
  // handle omitted semantics of Script
  handleOmittedScriptSemantics(
    algoRule: AlgoRule
  ) {
    let algo = new Algorithm();
    algo.head.kind = AlgoKind.STATIC;
    algo.steps = [{
      tokens: ["Return", "a", "new", "empty", "List", "."]
    }];

    algo.head.name = "Script0LexicallyDeclaredNames0";
    this.addAlgorithm(algoRule, copy(algo));
    algo.head.name = "Script0VarDeclaredNames0";
    this.addAlgorithm(algoRule, copy(algo));
    algo.head.name = "Script0LexicallyScopedDeclarations0";
    this.addAlgorithm(algoRule, copy(algo));
    algo.head.name = "Script0VarScopedDeclarations0";
    this.addAlgorithm(algoRule, copy(algo));
  }
}

// type map
export type TyMap = map<map<string>>

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
): map<string> => {
  // alias map for intrinsics
  const intrinsics: map<string> = {};

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
): map<string> => {
  // alias map for symbols
  const symbols: map<string> = {};

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
  baseMethods: map<string> = {}
) => {
  for (const tname in tyRule) {
    // initialization
    const methods: map<string> = copy(baseMethods);
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

export const replaceParams = (
  items: any[],
  params: string[]
): any[] => {
  let newItems = [];
  for (let i = 0; i < items.length; i ++) {
    let item = items[i];
    if (item.hasOwnProperty('items')) {
      newItems.push({ items: replaceParams(item['items'], params) });
    } else if (item.hasOwnProperty('tokens')) {
      newItems.push({ tokens: replaceParams(item['tokens'], params) });
    } else if (item.hasOwnProperty('id') && params.indexOf(item['id']) !== -1 && items[i+1] === "is" && items[i+2] === "present") {
      newItems.push({id: 'argumentsList'});
      newItems.push('[');
      newItems.push(params.indexOf(item['id']).toString());
      newItems.push(']');
    } else if (item.hasOwnProperty('id') && params.indexOf(item['id']) !== -1 && items[i+1] === "is" && items[i+2] === "not" && items[i+3] === "present") {
      newItems.push({id: 'argumentsList'});
      newItems.push('[');
      newItems.push(params.indexOf(item['id']).toString());
      newItems.push(']');
    } else {
      newItems.push(item);
    }
  }
  return newItems;
};
