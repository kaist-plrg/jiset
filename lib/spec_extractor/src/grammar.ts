import { ExtractorRule } from "./rule";
import { HTMLSemanticTag, HTMLTagType, TokenType } from "./enum";
import { getAllAttributes, norm, copy } from "./util";
import assert from "assert";

////////////////////////////////////////////////////////////////////////////////
// Grammar Structures
////////////////////////////////////////////////////////////////////////////////
// grammars
export class Grammar {
  constructor(
    public lexProds: Production[] = [],
    public prods: Production[] = [],
    public idxMap: IndexMap = {}
  ) { }

  // extract Grammar from a ECMAScript html file
  static from($: CheerioStatic, rule: ExtractorRule): Grammar {
    const grammar = new Grammar();

    // extract productions
    const sections = rule.grammar.sections;
    for (const section of sections) {
      grammar.merge(Grammar.fromSection($, rule, section));
    }

    // generate index maps
    grammar.generateIdxMap();

    return grammar;
  }

  // extract Grammar from a specific section
  static fromSection(
    $: CheerioStatic,
    rule: ExtractorRule,
    section: string
  ): Grammar {
    const grammar = new Grammar();
    const { lexProds, prods } = grammar;

    $(HTMLSemanticTag.PRODUCTION, `${ HTMLSemanticTag.APPENDIX }#${ section } > `)
      // filter production by rules
      .filter((_, elem) => {
        const attribs = getAllAttributes(elem);
        return !rule.grammar.excludes.includes(attribs.name.trim());
      })
      // extract production
      .each((_, prodElem) => {
        const prod = Production.from($, rule, prodElem);
        const attribs = getAllAttributes(prodElem);
        const isLexical = attribs.type && attribs.type === "lexical";
        const target = isLexical ? lexProds : prods;
        target.push(prod);
      });

    return grammar;
  }

  // generate index maps
  generateIdxMap() {
    const { idxMap, prods } = this;
    for (let prod of prods) {
      const { lhs, rhsList } = prod;
      let i = 0;

      for (let rhs of rhsList) {
        let names = [ `${ lhs.name }:` ];

        for (let token of rhs.tokens) {
          let newNames: string[] = [];
          switch (token.ty) {
            case TokenType.TERMINAL: {
              const { term } = token;
              names.forEach(_ => newNames.push(_ + term));
              break;
            }
            case TokenType.NON_TERMINAL: {
              const { optional, name } = token;
              names.forEach(_ => {
                if (optional) newNames.push(_);
                newNames.push(_ + name);
              });
              break;
            }
            case TokenType.BUT_NOT: {
              const { base, cases } = token;
              /* NOTE: base should be NonterminalToken */
              assert.equal(base.ty, TokenType.NON_TERMINAL);
              /* unsafe type casting */
              /* NOTE: [undefined, undefined, "adfasdf"].join("") == ["adfasdf"] */
              const butnot = cases.map(_ => (_ as any).name).join("");
              /* NOTE: unsafe type casting */
              names.forEach(_ => newNames.push(`${ _ }${ (base as NonterminalToken).name }butnot${ butnot }`));
              break;
            }
            default:
              newNames = names;
              break;
          }
          names = newNames;
        }

        let j = 0;
        for (let name of names) {
          idxMap[ norm(name) ] = { idx: i, subIdx: j };
          j++;
        }
        i++;
      }
    }
  }

  // merge with another Grammar
  merge(that: Grammar) {
    this.lexProds = this.lexProds.concat(that.lexProds);
    this.prods = this.prods.concat(that.prods);
  }

  // serialization
  serialize() {
    this.lexProds.forEach(_ => _.serialize());
    this.prods.forEach(_ => _.serialize());
  }
}

// productions
export class Production {
  constructor(
    public lhs: Lhs,
    public rhsList: Rhs[]
  ) { }

  static from(
    $: CheerioStatic,
    rule: ExtractorRule,
    prodElem: CheerioElement
  ): Production {
    // construct lhs
    let prodAttribs = getAllAttributes(prodElem);
    const { name, params, oneof } = prodAttribs;
    const lhs = new Lhs(name, params);

    // construct rhsList
    let rhsList: Rhs[] = $(prodElem)
      .children(HTMLSemanticTag.RHS)
      .map((_, rhsElem) => Rhs.from($, rule, rhsElem))
      .get();

    // handle oneof
    if (oneof)
      rhsList = rhsList[0].tokens.map(token => (new Rhs([token], "")));

    return new Production(lhs, rhsList);
  }

  // serialization
  serialize() {
    this.rhsList.forEach(_ => _.tokens = _.tokens.map(_ => serializeToken(_)));
  }
}

// index maps
export interface IndexMap {
  [ attr: string ]: {
    idx: number;
    subIdx: number;
  }
}

// left-hand-sides
export class Lhs {
  constructor(
    public name: string,
    public params: string[]
  ) { }
}

// right-hand-sides
export class Rhs {
  constructor(
    public tokens: Token[],
    public cond: string
  ) { }

  static from($: CheerioStatic, rule: ExtractorRule, rhsElem: CheerioElement): Rhs {
    // get constraints attributes
    const { constraints } = getAllAttributes(rhsElem);

    let tokens: Token[] = [];
    rhsElem.childNodes.forEach(tokenElem => {
      // ignore constraint tag
      if (tokenElem.tagName === HTMLSemanticTag.CONSTRAINTS) return;
      // extract token
      tokens.push(extractToken($, rule, tokens, tokenElem));
    });

    return new Rhs(tokens, constraints);
  }

  // get case name
  getCaseName(lhsName: string): string {
    let name = lhsName + ":";
    for (const token of this.tokens) {
      const elem: any = token
      if (token.ty === TokenType.TERMINAL) {
        name += token.term;
      } else if (token.ty === TokenType.NON_TERMINAL) {
        name += token.name;
      } else if (token.ty === TokenType.BUT_NOT) {
        name += token.base.name + "butnot" + elem.cases.map((x: any) => x.name).join("");
      }
    }
    return norm(name);
  }
}

// tokens
export type Token =
    SimpleToken
  | TerminalToken
  | NonterminalToken
  | UnicodeToken
  | ButNotToken
  | LookaheadToken

export interface SimpleToken {
  ty: TokenType.SIMPLE;
  data: string;
};

export interface TerminalToken {
  ty: TokenType.TERMINAL;
  term: string;
}

export interface NonterminalToken {
  ty: TokenType.NON_TERMINAL;
  name: string;
  optional: boolean;
  args: string[];
}

export interface UnicodeToken {
  ty: TokenType.UNICODE;
  code: string;
}

export interface ButNotToken {
  ty: TokenType.BUT_NOT;
  base: NonterminalToken;
  cases: Token[];
}

export interface LookaheadToken {
  ty: TokenType.LOOKAHEAD;
  contains: boolean;
  cases: Token[][];
}

////////////////////////////////////////////////////////////////////////////////
// Helper Functions
////////////////////////////////////////////////////////////////////////////////
// extract tokens
export function extractToken(
  $: CheerioStatic,
  rule: ExtractorRule,
  tokens: Token[],
  tokenElem: CheerioElement
): Token {
  const $t = $(tokenElem);
  const tokenAttribs = getAllAttributes(tokenElem);

  switch (tokenElem.tagName) {
    // nonterminal -> { name: "name", args: [], optional: boolean }
    case HTMLSemanticTag.NONTERM: {
      // name of nonterminal
      const name = $t.children(HTMLSemanticTag.ANCHOR).text().trim();
      // args, optional of nonterminal
      const { params, optional } = tokenAttribs;
      return { ty: TokenType.NON_TERMINAL, name, args: params, optional };
    }

    // terminal -> {term: "term"}
    case HTMLSemanticTag.TERM: {
      // term of terminal
      const term = $t.text().trim();
      return { ty: TokenType.TERMINAL, term };
    }

    // unicode or replaced by rule
    case HTMLSemanticTag.GPROSE: {
      const text = $t.text().trim();
      // check if text can be replaced
      if (rule.grammar.replaceRules[ text ])
        return { ty: TokenType.SIMPLE, data: rule.grammar.replaceRules[ text ] };
      // check if text is unicode
      if (text.startsWith(rule.grammar.unicode.hint) && text.endsWith(rule.grammar.unicode.hint2))
        return { ty: TokenType.UNICODE, code: text.substr(1, text.length - 2) };
      // other case is error
      throw new Error(`extractToken::${ HTMLSemanticTag.GPROSE }: cannot handle ${ text }`);
    }

    // but not
    case HTMLSemanticTag.GMOD: {
      /* NOTE: side-effect -> pop one element from tokens */
      const base = tokens.pop();
      if (!(base && base.ty === TokenType.NON_TERMINAL))
        throw new Error(`extractToken::${ HTMLSemanticTag.GMOD }: base token is not a non-terminal`);

      const firstChild = tokenElem.children[0];
      if (firstChild.data && firstChild.data.startsWith(`but only if`)) return base;

      const cases = tokenElem
        .childNodes
        // ignore text node
        .filter(_ => _.type === HTMLTagType.TAG)
        .map(token => extractToken($, rule, tokens, token));
      return { ty: TokenType.BUT_NOT, base, cases };
    }

    // lookahead or replaced by rule
    case HTMLSemanticTag.GANN: {
      const text = $t.text().trim();
      // check if text can be replaced
      if (rule.grammar.replaceRules[ text ])
        return { ty: TokenType.SIMPLE, data: rule.grammar.replaceRules[ text ] };
      // check if this is lookahead token
      if (text.startsWith(rule.grammar.lookahead.hint)) {
        // parse op code and extract contains
        const op = text[ rule.grammar.lookahead.opPos ];
        if (rule.grammar.lookahead.contains[ op ] === undefined)
          throw new Error(`extractToken::${ HTMLSemanticTag.GANN }: ${ op } is invalid lookahead op`);
        const contains: boolean = rule.grammar.lookahead.contains[ op ];

        // parse cases
        const cases: Array<Token[]> = []; let buf: Token[] = [];
        for (let i = 1; i < tokenElem.childNodes.length; i += 1) {
          let node = tokenElem.childNodes[ i ];
          // add token to buffer
          if (node.type === HTMLTagType.TAG)
            buf.push(extractToken($, rule, tokens, node));
          // add buffer to cases and flush buffer
          else if (node.type === HTMLTagType.TEXT && node.data && node.data.indexOf(",") > -1) {
            // clone buf
            cases.push(copy(buf));
            buf = [];
          }
        }
        // flush buffer
        if (buf.length > 0)
          cases.push(copy(buf));

        return { ty: TokenType.LOOKAHEAD, contains, cases };
      }

      throw new Error(`extractToken::${ HTMLSemanticTag.GANN }: cannot handle ${ text }`);
    }

    // otherwise
    default:
      throw new Error(`${ tokenElem.tagName }: extracting rhs error`);
  }
}

// serialize tokens (remove `ty` properties)
export const serializeToken = (token: Token): any => {
  switch (token.ty) {
    case TokenType.SIMPLE:
      return token.data;
    case TokenType.BUT_NOT: {
      const { base, cases } = token;
      return {
        base: serializeToken(base),
        cases: cases.map(_ => serializeToken(_))
      }
    }
    case TokenType.LOOKAHEAD: {
      const { contains, cases } = token;
      return {
        contains,
        cases: cases.map(tokenArr => tokenArr.map(_ => serializeToken(_)))
      }
    }
    default: {
      const tokenClone = copy(token);
      delete tokenClone.ty;
      return tokenClone;
    }
  }
}
