import { ExtractorRule, GrammarExtractResult, GrammarProduction, GrammarRHS, Token, IndexMap, NonterminalToken } from "./types";
import { HTMLSemanticTag, HTMLTagType, TokenType } from "./enum";
import { getAllAttributes, norm } from "./util";
import assert from "assert";

export const generateIdxMap = ( result: GrammarExtractResult ): IndexMap => {
  let idxMap: IndexMap = {};
  for ( let prod of result.prods ) {
    const { lhs, rhsList } = prod;
    let i = 0;

    for ( let rhs of rhsList ) {
      let names = [ `${ lhs.name }:` ];

      for ( let token of rhs.tokens ) {
        let newNames: string[] = [];
        switch ( token._type ) {
          case TokenType.TERMINAL: {
            const { term } = token;
            names.forEach( _ => newNames.push( _ + term ) );
            break;
          }
          case TokenType.NON_TERMINAL: {
            const { optional, name } = token;
            names.forEach( _ => {
              if ( optional ) newNames.push( _ );
              newNames.push( _ + name );
            } );
            break;
          }
          case TokenType.BUT_NOT: {
            const { base, cases } = token;
            /* NOTE : base should be NonterminalToken */
            assert.equal( base._type, TokenType.NON_TERMINAL );
            /* unsafe type casting */
            /* NOTE : [undefined, undefined, "adfasdf"].join("") == ["adfasdf"] */
            const butnot = cases.map( _ => ( _ as any ).name ).join( "" );
            /* NOTE : unsafe type casting */
            names.forEach( _ => newNames.push( `${ _ }${ ( base as NonterminalToken ).name }butnot${ butnot }` ) );
            break;
          }
          default:
            newNames = names;
            break;
        }
        names = newNames;
      }

      let j = 0;
      for ( let name of names ) {
        idxMap[ norm( name ) ] = { idx: i, subIdx: j };
        j++;
      }
      i++;
    }
  }

  return idxMap;
}

export const serializeProduction = ( prod: GrammarProduction ): any => {
  const { lhs, rhsList } = prod;

  return {
    lhs,
    rhsList: rhsList.map( rhs => {
      const { tokens, cond } = rhs;
      return {
        tokens: tokens.map( _ => serializeToken( _ ) ),
        cond
      };
    } )
  }
}

/* erase _type in Token object */
export const serializeToken = ( token: Token ): any => {
  switch ( token._type ) {
    case TokenType.SIMPLE:
      return token.data;
    case TokenType.BUT_NOT: {
      const { base, cases } = token;
      return {
        base: serializeToken( base ),
        cases: cases.map( _ => serializeToken( _ ) )
      }
    }
    case TokenType.LOOKAHEAD: {
      const { contains, cases } = token;
      return {
        contains,
        cases: cases.map( tokenArr => tokenArr.map( _ => serializeToken( _ ) ) )
      }
    }
    default: {
      const tokenClone = JSON.parse( JSON.stringify( token ) );
      delete tokenClone._type;
      return tokenClone;
    }
  }
}

interface ExtractSectionArgs {
  section: string;
  rule: ExtractorRule;
  $: CheerioStatic
}
export const extractSection
  = ( args: ExtractSectionArgs ): GrammarExtractResult => {
    const { $, rule, section } = args;
    const result: GrammarExtractResult = {
      lexProds: [],
      prods: []
    };

    $( HTMLSemanticTag.PRODUCTION, `${ HTMLSemanticTag.APPENDIX }#${ section } > ` )
      // filter production by rules
      .filter( ( _, elem ) => {
        let attribs = getAllAttributes( elem );
        return !rule.grammar.excludes.includes( attribs.name.trim() );
      } )
      // extract production
      .each( ( _, prodElem ) => {
        const prod = extractProduction( { $, prodElem, rule } );
        let attribs = getAllAttributes( prodElem );
        const target = attribs.type && attribs.type === "lexical" ? result.lexProds : result.prods;
        target.push( prod );
      } );

    return result;
  }


interface ExtractProductionArgs {
  $: CheerioStatic,
  prodElem: CheerioElement,
  rule: ExtractorRule
}
const extractProduction
  = ( args: ExtractProductionArgs ): GrammarProduction => {
    const { $, prodElem, rule } = args;

    // construct lhs
    let prodAttribs = getAllAttributes( prodElem );
    const { name, params, oneof } = prodAttribs;
    const lhs = { name, params };

    // construct rhsList
    let rhsList: GrammarRHS[] = $( prodElem )
      .children( HTMLSemanticTag.RHS )
      .map( ( _, rhsElem ) => extractRHS( { $, rhsElem, rule } ) )
      .get();

    // handle oneof
    if ( oneof ) {
      rhsList = rhsList[ 0 ].tokens.map( token => ( {
        tokens: [ token ],
        cond: ""
      } ) );
    }

    // handle IfStatement -> swap rhs to resolve unambiguity
    if ( name === "IfStatement" ) {
      // IfStatement should have 2 production rule
      assert.equal( rhsList.length, 2 );
      // swap first and second production rule
      [ rhsList[ 0 ], rhsList[ 1 ] ] = [ rhsList[ 1 ], rhsList[ 0 ] ];
    }

    return {
      lhs,
      rhsList
    };
  }

interface ExtractRHSArgs {
  $: CheerioStatic,
  rhsElem: CheerioElement,
  rule: ExtractorRule
};
const extractRHS =
  ( args: ExtractRHSArgs ): GrammarRHS => {
    const { $, rhsElem, rule } = args;
    // get constraints attributes
    const { constraints } = getAllAttributes( rhsElem );

    let tokens: Token[] = [];
    rhsElem.childNodes.forEach( tokenElem => {
      // ignore constraint tag
      if ( tokenElem.tagName === HTMLSemanticTag.CONSTRAINTS )
        return;
      // extract token
      tokens.push( extractToken( tokens, { $, tokenElem, rule } ) );
    } );

    return {
      tokens,
      cond: constraints
    }
  }

interface ExtractTokenArgs {
  $: CheerioStatic,
  tokenElem: CheerioElement,
  rule: ExtractorRule
};
const extractToken =
  ( tokens: Token[], args: ExtractTokenArgs ): Token => {
    const { tokenElem, $, rule } = args;
    const $t = $( tokenElem );
    const tokenAttribs = getAllAttributes( tokenElem );

    switch ( tokenElem.tagName ) {
      // nonterminal -> {name: "name", args: [], optional: boolean }
      case HTMLSemanticTag.NONTERM: {
        // name of nonterminal
        const name = $t.children( HTMLSemanticTag.ANCHOR ).text().trim();
        // args, optional of nonterminal
        const { params, optional } = tokenAttribs;
        return { _type: TokenType.NON_TERMINAL, name, args: params, optional };
      }
      // terminal -> {term: "term"}
      case HTMLSemanticTag.TERM: {
        // term of terminal
        const term = $t.text().trim();
        return { _type: TokenType.TERMINAL, term };
      }
      // unicode or replaced by rule
      case HTMLSemanticTag.GPROSE: {
        const text = $t.text().trim();
        // check if text can be replaced
        if ( rule.grammar.replaceRules[ text ] )
          return { _type: TokenType.SIMPLE, data: rule.grammar.replaceRules[ text ] };
        // check if text is unicode
        if ( text.startsWith( rule.grammar.unicode.hint ) && text.endsWith( rule.grammar.unicode.hint2 ) )
          return { _type: TokenType.UNICODE, code: text.substr( 1, text.length - 2 ) };
        // other case is error
        throw new Error( `extractToken::${ HTMLSemanticTag.GPROSE } : cannot handle ${ text }` );
      }
      // but not
      case HTMLSemanticTag.GMOD: {
        /* NOTE : side-effect -> pop one element from tokens */
        const base = tokens.pop();
        if ( base === undefined )
          throw new Error( `extractToken::${ HTMLSemanticTag.GMOD } : tokens is empty` );

        const cases = tokenElem
          .childNodes
          // ignore text node
          .filter( _ => _.type === HTMLTagType.TAG )
          .map( token => extractToken( tokens, { $, tokenElem: token, rule } ) );
        return { _type: TokenType.BUT_NOT, base, cases };
      }
      // lookahead or replaced by rule
      case HTMLSemanticTag.GANN: {
        const text = $t.text().trim();
        // check if text can be replaced
        if ( rule.grammar.replaceRules[ text ] )
          return { _type: TokenType.SIMPLE, data: rule.grammar.replaceRules[ text ] };
        // check if this is lookahead token
        if ( text.startsWith( rule.grammar.lookahead.hint ) ) {
          // parse op code and extract contains
          const op = text[ rule.grammar.lookahead.opPos ];
          if ( rule.grammar.lookahead.contains[ op ] === undefined )
            throw new Error( `extractToken::${ HTMLSemanticTag.GANN } : ${ op } is invalid lookahead op` );
          const contains: boolean = rule.grammar.lookahead.contains[ op ];

          // parse cases
          const cases: Array<Token[]> = []; let buf: Token[] = [];
          for ( let i = 1; i < tokenElem.childNodes.length; i += 1 ) {
            let node = tokenElem.childNodes[ i ];
            // add token to buffer
            if ( node.type === HTMLTagType.TAG )
              buf.push( extractToken( tokens, { $, rule, tokenElem: node } ) );
            // add buffer to cases and flush buffer
            else if ( node.type === HTMLTagType.TEXT && node.data && node.data.indexOf( "," ) > -1 ) {
              // clone buf
              cases.push( JSON.parse( JSON.stringify( buf ) ) );
              buf = [];
            }
          }
          // flush buffer
          if ( buf.length > 0 )
            cases.push( JSON.parse( JSON.stringify( buf ) ) );

          return { _type: TokenType.LOOKAHEAD, contains, cases };
        }

        throw new Error( `extractToken::${ HTMLSemanticTag.GANN } : cannot handle ${ text }` );
      }
      default:
        throw new Error( `${ tokenElem.tagName } : extracting rhs error` );
    }
  }