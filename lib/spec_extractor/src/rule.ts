import { AliasMap } from "./types";

// extracting rule
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
  intrinsics: {
    table: string;
  };
  symbols: {
    table: string;
  };
  tyRule: TyRule;
  algoRule: AlgoRule;
}

// type rule
export interface TyRule {
  [ attr: string ]: string | {
    id: string;
    prefix?: string;
    thisName?: string;
    children?: TyRule;
  };
}

// algorithm rule
export interface AlgoRule {
  replacePrefix: {
    [ name: string ]: string;
  }
  replaceParams: {
    [ name: string ]: string[];
  };
  ignores: string[];
}
