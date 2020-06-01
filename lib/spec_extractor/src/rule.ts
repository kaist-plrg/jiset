import { AliasMap } from "./types";
import { Step } from "./algorithm";

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
  replacePrefix: map<string>;
  replaceGrammarAlgo: map<GrammarAlgo>;
  replaceNames: map<string>;
  replaceParams: map<string[]>;
  preSteps: map<Step[]>;
  replaceSteps: map<ReplaceStep[]>;
  replaceLength: map<number>;
  forwards: map<string[]>;
  ignores: string[];
  globalElementIds: string[];
  grammarElementIds: string[];
}

interface map<T> {
  [ prop: string ]: T;
}
interface GrammarAlgo {
  name: string;
  moreParams: string[];
}
interface ReplaceStep {
  idxList: number[];
  item: any
}
