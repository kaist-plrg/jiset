import { Grammar } from "./grammar";
import { ExtractorRule, TyRule } from "./rule";
import { HTMLSemanticTag } from "./enum";
import { norm, unwrap, copy } from "./util";
import { AliasMap } from "./types";
import { Algorithm } from "./algorithm";
import assert from "assert";

////////////////////////////////////////////////////////////////////////////////
// Spec Structures
////////////////////////////////////////////////////////////////////////////////
export class Spec {
  constructor(
    public consts: string[],
    public globalMethods: string[],
    public grammar: Grammar,
    public intrinsics: AliasMap,
    public symbols: AliasMap,
    public tys: TyMap
  ) { }

  // extract Spec from a ECMAScript html file
  static from(
    $: CheerioStatic,
    rule: ExtractorRule
  ) {
    const consts = extractConsts($);
    const globalMethods: string[] = []; // TODO
    const grammar = Grammar.from($, rule);
    const intrinsics = extractIntrinsics($, rule.intrinsics.table);
    const symbols = extractSymbols($, rule.symbols.table);
    const tys = extractTypes($, rule.tys, grammar);

    return new Spec(
      consts,
      globalMethods,
      grammar,
      intrinsics,
      symbols,
      tys
    );
  }
}

// type map
export interface TyMap {
  [ attr: string ]: AliasMap
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
  grammar: Grammar,
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
        const algo = Algorithm.from($, elem, grammar);
        let algoName = algo.head.name;
        let name = algoName;
        if (algoName.startsWith(prefix)) {
          algoName = unwrap(algoName, prefix.length);
          name = `${tname}.${algoName}`;
          methods[algoName] = name;
          algo.head.params = [ thisName ].concat(algo.head.params);
        }
        // TODO addMethod(name, algo.length, algo.body);
      });

    // recursively extract children types
    extractTypes($, children, grammar, tys, prefix, thisName, methods);
  }

  return tys;
}
