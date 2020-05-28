import { TokenType, AlgoKind } from "./enum";

export interface ExtractorRule {
  grammar: {
    sections: string[];
    excludes: string[];
    replaceRules: { [ attr: string ]: string };
    lookahead: {
      hint: string;
      opPos: number;
      contains: { [ attr: string ]: boolean };
    },
    unicode: {
      hint: string;
      hint2: string;
    }
  };
}

/**
 * types for Grammar
 */
export interface GrammarExtractResult {
  lexProds: GrammarProduction[];
  prods: GrammarProduction[];
  idxMap?: IndexMap;
}

export interface GrammarProduction {
  lhs: GrammarLHS;
  rhsList: GrammarRHS[];
}

export interface GrammarLHS {
  name: string;
  params: string[];
}

export interface GrammarRHS {
  tokens: Token[];
  cond: string;
}

export interface IndexMap {
  [ attr: string ]: {
    idx: number;
    subIdx: number;
  }
}

/* types for token */
export type Token = SimpleToken
  | TerminalToken
  | NonterminalToken
  | UnicodeToken
  | ButNotToken
  | LookaheadToken;

interface SimpleToken {
  _type: TokenType.SIMPLE;
  data: string;
};

interface TerminalToken {
  _type: TokenType.TERMINAL;
  term: string;
}

export interface NonterminalToken {
  _type: TokenType.NON_TERMINAL;
  name: string;
  optional: boolean;
  args: string[];
}

interface UnicodeToken {
  _type: TokenType.UNICODE;
  code: string;
}

interface ButNotToken {
  _type: TokenType.BUT_NOT;
  base: Token;
  cases: Token[];
}

interface LookaheadToken {
  _type: TokenType.LOOKAHEAD;
  contains: boolean;
  cases: Array<Token[]>;
}

/**
 * types for algorithm
 */

export interface AlgoClause {
  name: string;
  length: number;
  body: {
    kind: AlgoKind;
    params: string[];
  } & AlgoBody;
}

export interface AlgoBody {
  steps: AlgoStep[];
}

export interface AlgoStep {
  tokens: AlgoToken[];
}

export type AlgoToken = AlgoTextToken
  | AlgoRefToken
  | AlgoVarToken
  | AlgoValueToken
  | AlgoSubStepToken;

export type AlgoRefToken = string;
export type AlgoTextToken = string;
export interface AlgoVarToken { id: string; }
export interface AlgoValueToken { value: string; }
export type AlgoSubStepToken = AlgoBody;