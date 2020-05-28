export enum ECMAScriptVersion {
  ES2020 = "ES2020",
  ES2019 = "ES2019",
  ES2018 = "ES2018",
  ES2017 = "ES2017",
  ES2016 = "ES2016",
};

export enum HTMLSemanticTag {
  PRODUCTION = "emu-production",
  APPENDIX = "emu-annex",
  RHS = "emu-rhs",
  NONTERM = "emu-nt",
  TERM = "emu-t",
  GPROSE = "emu-gprose",
  GMOD = "emu-gmod",
  GANN = "emu-gann",
  CONSTRAINTS = "emu-constraints",
  CLAUSE = "emu-clause",
  ALGO = "emu-alg",
  REF = "emu-xref",
  VALUE = "emu-val",

  ANCHOR = "a",
  HEADER = "h1",
  UNORDERED_LIST = "ul",
  ORDERED_LIST = "ol",
  LIST_ITEM = "li",
  VARIABLE = "var",
};

export enum HTMLTagType {
  TAG = "tag",
  TEXT = "text"
}

export enum HTMLTagAttribute {
  PARAMS = "params",
  OPTIONAL = "optional",
  CONSTRAINTS = "constraints",
  ONEOF = "oneof",
  AOID = "aoid",
}

export enum TokenType {
  TERMINAL = "TokenType/TERMINAL",
  NON_TERMINAL = "TokenType/NON_TERMINAL",
  UNICODE = "TokenType/UNICODE",
  BUT_NOT = "TokenType/BUT_NOT",
  SIMPLE = "TokenType/SIMPLE",
  LOOKAHEAD = "TokenType/LOOKAHEAD"
}

export enum AlgoKind {
  METHOD = "Method",
  STATIC = "StaticSemantics",
  RUNTIME = "RuntimeSemantics",
}
