import { HTMLSemanticTag, HTMLTagType, HTMLTagAttribute, AlgoKind } from "./enum";
import { Grammar } from "./grammar";
import { norm } from "./util";
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
    elem: CheerioElement,
    grammar: Grammar
  ): Algorithm {
    const head = Head.from($, elem);
    const steps = Algorithm.getSteps($, elem.children[0], grammar);
    return new Algorithm(head, steps);
  }

  static getSteps(
    $: CheerioStatic,
    elem: CheerioElement,
    grammar: Grammar
  ): Step[] {
    return []; // TODO
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

  static getParams(str: string): string[] {
    str = str.substring(str.indexOf("("));
    return (str.match(/[^\s,()\[\]]+/g) || []);
  }

  static getLength(str: string): number {
    str = str.substring(str.indexOf("("));
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
export interface SupToken { sup: string; }
export interface UrlToken { url: string; }
export interface GrammarToken { grammar: string; subs: string[]; }




// interface ExtractAlgoClauseArgs {
//   $: CheerioStatic;
//   clauseId: string;
// }
// // extract emu-clause
// // <emu-clause>
// //   <h1>
// //     <var/>
// //     <var/>
// //   </h1>
// //   <emu-alg>
// //     <ol></ol>
// //   </emu-alg>
// // </emu-clause>
// export const extractAlgoClause =
//   (args: ExtractAlgoClauseArgs): AlgoClause => {
//     const { $, clauseId } = args;
//     // NOTE : clauseId is unique
//     assert.equal($(`${ HTMLSemanticTag.CLAUSE }#${ clauseId }`).length, 1);
//     const clauseElem = $(`${ HTMLSemanticTag.CLAUSE }#${ clauseId }`)[ 0 ];
// 
//     // extract name
//     const name: string = getAllAttributes(clauseElem)[ HTMLTagAttribute.AOID ];
//     const length = 0;
// 
//     // extract params (NOTE : unsafe type cast)
//     const params: string[] =
//       $(`${ HTMLSemanticTag.HEADER } > ${ HTMLSemanticTag.VARIABLE }`, clauseElem)
//         .map((_, elem) => $(elem).text())
//         .get();
// 
//     // extract steps
//     // <emu-alg><ol></ol></emu-alg>
// 
//     // NOTE
//     assert.equal($(`${ HTMLSemanticTag.ALGO } > ${ HTMLSemanticTag.ORDERED_LIST }`, clauseElem).length, 1);
//     const algoElem =
//       $(`${ HTMLSemanticTag.ALGO } > ${ HTMLSemanticTag.ORDERED_LIST }`, clauseElem)[ 0 ];
// 
//     return {
//       name,
//       length,
//       body: {
//         kind: AlgoKind.METHOD,
//         params,
//         steps: extractAlgoBody({ $, elem: algoElem }).steps
//       }
//     }
//   }
// 
// interface ExtractAlgoArgs {
//   $: CheerioStatic;
//   elem: CheerioElement;
// }
// // extract ol : return {steps: []}
// export const extractAlgoBody =
//   (args: ExtractAlgoArgs): AlgoBody => {
//     const { $, elem } = args;
//     return {
//       steps: $(`> ${ HTMLSemanticTag.LIST_ITEM }`, elem)
//         .map((_, elem) => extractAlgoStep({ $, elem }))
//         .get()
//     }
//   }
// 
// interface extractAlgoStepArgs {
//   $: CheerioStatic;
//   elem: CheerioElement;
// }
// // extract li : return {tokens: []}
// export const extractAlgoStep =
//   (args: extractAlgoStepArgs): AlgoStep => {
//     const { $, elem } = args;
// 
//     return {
//       tokens: elem
//         .childNodes
//         // map each nodes to token[]
//         .map(elem => extractToken({ elem, $ }))
//         // flatten
//         .reduce((accTokens, tokens) => {
//           return accTokens.concat(tokens);
//         }, [])
//     };
//   }
// 
// interface ExtractTokenArgs {
//   $: CheerioStatic;
//   elem: CheerioElement;
// }
// // return AlgoToken[]
// export const extractToken =
//   (args: ExtractTokenArgs): AlgoToken[] => {
//     const { $, elem } = args;
//     switch (elem.type) {
//       case HTMLTagType.TAG: {
//         /* TODO : handle various semantic tag */
//         switch (elem.tagName) {
//           case HTMLSemanticTag.REF:
//             return splitText($(elem).text());
//           case HTMLSemanticTag.VALUE:
//             return [ { value: $(elem).text() } ];
//           case HTMLSemanticTag.VARIABLE:
//             return [ { id: $(elem).text() } ];
//           case HTMLSemanticTag.ORDERED_LIST:
//             return [ extractAlgoBody({ $, elem }) ];
//           default:
//             console.log(`name : ${ elem.name }, type : ${ elem.type }, tag : ${ elem.tagName }`);
//             throw new Error(`extractToken : Unhandled tag name(${ elem.tagName })`);
//         }
//       }
//       case HTMLTagType.TEXT:
//         /* split text to token */
//         return splitText($(elem).text());
//       default:
//         throw new Error(`extractToken : Unhandled tag type(${ elem.type })`)
//     }
//   }
