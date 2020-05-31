import { Token } from "./grammar";
import { TokenType, AlgoKind } from "./enum";

// alias map
export interface AliasMap {
  [ attr: string ]: string
}

// algorithm clauses
export interface AlgoClause {
  name: string;
  length: number;
  body: {
    kind: AlgoKind;
    params: string[];
  } & AlgoBody;
}

// algorithm bodies
export interface AlgoBody {
  steps: AlgoStep[];
}

// algorithm steps
export interface AlgoStep {
  tokens: AlgoToken[];
}

// algorithm tokens
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
