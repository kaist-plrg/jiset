import { HTMLSemanticTag, HTMLTagType, HTMLTagAttribute } from "./enum";
import { AlgoKind, TokenType } from "./enum";
import { Grammar, Rhs } from "./grammar";
import { ExtractorRule } from "./rule";
import { norm, splitText, getAllAttributes } from "./util";
import assert from "assert";

////////////////////////////////////////////////////////////////////////////////
// Algorithm Structures
////////////////////////////////////////////////////////////////////////////////
// algorihtms
export class Algorithm {
  constructor(
    public head: Head = new Head(),
    public steps: Step[] = []
  ) { }

  // extract Algorithm from a specific HTMLElement
  static from(
    $: CheerioStatic,
    rule: ExtractorRule,
    elem: CheerioElement,
    grammar: Grammar
  ): Algorithm {
    const head = Head.from($, elem);
    const steps = Step.listFrom($, rule, elem.children[0], grammar);
    return new Algorithm(head, steps);
  }
}

// algorithm heads
export class Head {
  constructor(
    public name: string = "",
    public kind: AlgoKind = AlgoKind.METHOD,
    public lang: boolean = false,
    public params: string[] = [],
    public length: number = 0
  ) { }

  // extract Head from a specific HTMLElement
  static from(
    $: CheerioStatic,
    elem: CheerioElement
  ): Head {
    const head = $($(HTMLSemanticTag.HEADER, elem.parent)[0]);
    const secnoElem = head.children()[0];
    const secno = $(secnoElem).text();
    const str = norm(head.text().slice(secno.length));

    if (str === "") throw new Error(`Head.from: no algorithm head`);

    // extract algorithm name
    let name: string =
      (str.indexOf("(") == -1 ? str : str.substring(0, str.indexOf("(")))
        .replace(/.*Semantics:/g, "");

    // extract kind
    const kind: AlgoKind =
      str.startsWith("StaticSemantics") ? AlgoKind.STATIC :
      str.startsWith("RuntimeSemantics") ? AlgoKind.RUNTIME :
      AlgoKind.METHOD;

    // assume algorithms not for core JS language at first
    const lang = false

    // extract parameters
    const params = Head.getParams(str);

    // extract length
    const length = Head.getLength(str);

    return new Head(name, kind, lang, params, length);
  }

  // extract parameters
  static getParams(str: string): string[] {
    const from = str.indexOf("(");
    if (from == -1) return [];
    str = str.substring(from);
    return (str.match(/[^\s,()\[\]]+/g) || []);
  }

  // extract length of parameters
  static getLength(str: string): number {
    const from = str.indexOf("(");
    if (from == -1) return 0;
    str = str.substring(from);
    const idx1 = str.indexOf("[");
    const idx2 = str.indexOf("...");
    const idx =
      (idx1 != -1 && idx2 != -1) ? Math.min(idx1, idx2) :
      (idx1 != -1) ? idx1 :
      (idx2 != -1) ? idx2 :
      str.length;
    str = str.substring(0, idx);
    return (str.match(/[^\s,()\[\]]+/g) || []).length;
  }
}

// algorithm steps
export class Step {
  constructor(
    public tokens: Token[] = []
  ) { }

  // extract Step from a specific HTMLElement
  static from(
    $: CheerioStatic,
    rule: ExtractorRule,
    elem: CheerioElement,
    grammar: Grammar
  ): Step {
    const blocks = Array.from(elem.children);
    const tokens = blocks.flatMap<Token>(block => {
      switch (block.type) {
        case HTMLTagType.TEXT: return splitText(block.data);
        case HTMLTagType.TAG:
          const text = $(block).text();
          switch (block.name) {
            // special tokens
            case HTMLSemanticTag.CONST: return { const: text };
            case HTMLSemanticTag.PREFORMAT:
            case HTMLSemanticTag.CODE: return { code: text };
            case HTMLSemanticTag.VALUE: return { value: text };
            case HTMLSemanticTag.VARIABLE: return { id: text };
            case HTMLSemanticTag.NONTERM: return { nt: text };
            case HTMLSemanticTag.ANCHOR: return { url: text };

            // superscript tokens
            case HTMLSemanticTag.SUPERSCRIPT:
              return { sup: Step.from($, rule, block, grammar) };

            // grammar tokens
            case HTMLSemanticTag.GRAMMAR:
              // TODO
              const prod = block.children[0];
              const lhsName = getAllAttributes(prod).name;
              const rhsElem = prod.children[2];
              const rhs = Rhs.from($, rule, rhsElem);
              let name = rhs.getCaseName(lhsName);

              if (name in grammar.idxMap) {
                const subs: string[] = [];
                for (const token of rhs.tokens) {
                  if (token.ty === TokenType.NON_TERMINAL) subs.push(token.name);
                }
                name = lhsName + grammar.idxMap[name].idx;
                return { grammar: "not-found", subs: subs };
              } else return { grammar: "not-found", subs: [] };

            // sub-steps
            case HTMLSemanticTag.ORDERED_LIST:
            case HTMLSemanticTag.UNORDERED_LIST:
              return { steps: Step.listFrom($, rule, block, grammar) };

            // text tokens
            case HTMLSemanticTag.REF:
            case HTMLSemanticTag.SUBSCRIPT:
            case HTMLSemanticTag.ITALIC:
            case HTMLSemanticTag.BOLD:
              return splitText(text);
          }
      }
      console.error(block);
      throw new Error(`Step.from: Unhandled token`);
    });
    return new Step(tokens);
  }

  // extract list of Step from a specific HTMLElement
  static listFrom(
    $: CheerioStatic,
    rule: ExtractorRule,
    list: CheerioElement,
    grammar: Grammar
  ): Step[] {
    return list.children.map(_ => Step.from($, rule, _, grammar));
  }
}

// algorithm tokens
export type Token =
    ConstToken
  | CodeToken
  | ValueToken
  | IdToken
  | StepsToken
  | NonterminalToken
  | TextToken
  | SupToken
  | UrlToken
  | GrammarToken

export interface ConstToken { const: string; }
export interface CodeToken { code: string; }
export interface ValueToken { value: string; }
export interface IdToken { id: string; }
export interface StepsToken { steps: Step[]; }
export interface NonterminalToken { nt: string; }
export type TextToken = string;
export interface SupToken { sup: Step; }
export interface UrlToken { url: string; }
export interface GrammarToken { grammar: string; subs: string[]; }
