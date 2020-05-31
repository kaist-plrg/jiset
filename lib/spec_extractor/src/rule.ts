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
  tys: TyRule;
}

export interface TyRule {
  [ attr: string ]: string | {
    id: string;
    prefix: string;
    children: TyRule;
  };
}
