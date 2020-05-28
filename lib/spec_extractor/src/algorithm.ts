import { HTMLSemanticTag, HTMLTagType, HTMLTagAttribute, AlgoKind } from "./enum";
import { AlgoToken, AlgoStep, AlgoBody, AlgoClause } from "./types";
import { splitText, getAllAttributes } from "./util";
import assert from "assert";

interface ExtractAlgoClauseArgs {
  $: CheerioStatic;
  clauseId: string;
}
// extract emu-clause
// <emu-clause>
//   <h1>
//     <var/>
//     <var/>
//   </h1>
//   <emu-alg>
//     <ol></ol>
//   </emu-alg>
// </emu-clause>
export const extractAlgoClause =
  (args: ExtractAlgoClauseArgs): AlgoClause => {
    const { $, clauseId } = args;
    // NOTE : clauseId is unique
    assert.equal($(`${ HTMLSemanticTag.CLAUSE }#${ clauseId }`).length, 1);
    const clauseElem = $(`${ HTMLSemanticTag.CLAUSE }#${ clauseId }`)[ 0 ];

    // extract name
    const name: string = getAllAttributes(clauseElem)[ HTMLTagAttribute.AOID ];
    const length = 0;

    // extract params (NOTE : unsafe type cast)
    const params: string[] =
      $(`${ HTMLSemanticTag.HEADER } > ${ HTMLSemanticTag.VARIABLE }`, clauseElem)
        .map((_, elem) => $(elem).text())
        .get();

    // extract steps
    // <emu-alg><ol></ol></emu-alg>

    // NOTE
    assert.equal($(`${ HTMLSemanticTag.ALGO } > ${ HTMLSemanticTag.ORDERED_LIST }`, clauseElem).length, 1);
    const algoElem =
      $(`${ HTMLSemanticTag.ALGO } > ${ HTMLSemanticTag.ORDERED_LIST }`, clauseElem)[ 0 ];

    return {
      name,
      length,
      body: {
        kind: AlgoKind.METHOD,
        params,
        steps: extractAlgoBody({ $, elem: algoElem }).steps
      }
    }
  }

interface ExtractAlgoArgs {
  $: CheerioStatic;
  elem: CheerioElement;
}
// extract ol : return {steps: []}
export const extractAlgoBody =
  (args: ExtractAlgoArgs): AlgoBody => {
    const { $, elem } = args;
    return {
      steps: $(`> ${ HTMLSemanticTag.LIST_ITEM }`, elem)
        .map((_, elem) => extractAlgoStep({ $, elem }))
        .get()
    }
  }

interface extractAlgoStepArgs {
  $: CheerioStatic;
  elem: CheerioElement;
}
// extract li : return {tokens: []}
export const extractAlgoStep =
  (args: extractAlgoStepArgs): AlgoStep => {
    const { $, elem } = args;

    return {
      tokens: elem
        .childNodes
        // map each nodes to token[]
        .map(elem => extractToken({ elem, $ }))
        // flatten
        .reduce((accTokens, tokens) => {
          return accTokens.concat(tokens);
        }, [])
    };
  }

interface ExtractTokenArgs {
  $: CheerioStatic;
  elem: CheerioElement;
}
// return AlgoToken[]
export const extractToken =
  (args: ExtractTokenArgs): AlgoToken[] => {
    const { $, elem } = args;
    switch (elem.type) {
      case HTMLTagType.TAG: {
        /* TODO : handle various semantic tag */
        switch (elem.tagName) {
          case HTMLSemanticTag.REF:
            return splitText($(elem).text());
          case HTMLSemanticTag.VALUE:
            return [ { value: $(elem).text() } ];
          case HTMLSemanticTag.VARIABLE:
            return [ { id: $(elem).text() } ];
          case HTMLSemanticTag.ORDERED_LIST:
            return [ extractAlgoBody({ $, elem }) ];
          default:
            console.log(`name : ${ elem.name }, type : ${ elem.type }, tag : ${ elem.tagName }`);
            throw new Error(`extractToken : Unhandled tag name(${ elem.tagName })`);
        }
      }
      case HTMLTagType.TEXT:
        /* split text to token */
        return splitText($(elem).text());
      default:
        throw new Error(`extractToken : Unhandled tag type(${ elem.type })`)
    }
  }
