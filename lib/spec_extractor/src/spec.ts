import { Grammar } from "./grammar";
import { ExtractorRule, TyRule } from "./rule";
import { HTMLSemanticTag } from "./enum";
import { norm, unwrap, copy } from "./util";
import { AliasMap } from "./types";
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
    // constants
    const consts = extractConsts($);

    // global methods
    const globalMethods: string[] = []; // TODO

    // grammars
    const grammar = Grammar.from($, rule);

    // intrinsics
    const intrinsics = extractIntrinsics($, rule.intrinsics.table);

    // symbols
    const symbols = extractSymbols($, rule.symbols.table);

    // types
    const tys = extractTypes($, rule.tys);

    // specifications
    const spec = new Spec(
      consts,
      globalMethods,
      grammar,
      intrinsics,
      symbols,
      tys
    );

    return spec;
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
  const intrinsics: AliasMap = {};
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
  const symbols: AliasMap = {};
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
  tys: TyMap = {},
  basePrefix: string = "",
  baseMethods: AliasMap = {}
) => {
  for (const tname in tyRule) {
    // initialization
    const methods: AliasMap = copy(baseMethods);
    tys[tname] = methods;
    let info = tyRule[tname];
    if (typeof info === "string") info = { id: info, prefix: basePrefix, children: {} };
    const { id, prefix, children } = info;

    // extract algorithms and link member methods
    $(HTMLSemanticTag.ALGO, `${HTMLSemanticTag.CLAUSE}#${id}`)
      .each((_, elem) => {
        // TODO const algo = Algorithm.from($, rule, elem, grammar);
        // TODO let algoName = algo.name;
        let algoName = getAlgoName($, elem); // TODO remove
        if (algoName.startsWith(prefix)) {
          algoName = unwrap(algoName, prefix.length);
          const name = `${tname}.${algoName}`;
          methods[algoName] = name;
        }
      });

    // recursively extract children types
    extractTypes($, children, tys, prefix, methods);
  }

  return tys;
}

// TODO remove
export const getAlgoName = (
  $: CheerioStatic,
  elem: CheerioElement
): string => {
  const head = $("h1", elem.parent);
  const secnoElem = head.children()[0];
  const secno = $(secnoElem).text();
  const str = head.text().slice(secno.length);

  let name: string =
    str.indexOf("(") == -1 ? str : str.substring(0, str.indexOf("("));
  name = norm(name).replace(/.*Semantics:/g, "");

  return name;
}
