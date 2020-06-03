export enum ECMAScriptVersion {
  es2020 = "es2020",
  es2019 = "es2019",
  es2018 = "es2018",
  es2017 = "es2017",
  es2016 = "es2016",
};

export enum HTMLSemanticTag {
  ALGO = "emu-alg",
  APPENDIX = "emu-annex",
  CLAUSE = "emu-clause",
  CONST = "emu-const",
  CONSTRAINTS = "emu-constraints",
  GANN = "emu-gann",
  GMOD = "emu-gmod",
  GPROSE = "emu-gprose",
  GRAMMAR = "emu-grammar",
  MODS = "emu-mods",
  NONTERM = "emu-nt",
  PRODUCTION = "emu-production",
  REF = "emu-xref",
  RHS = "emu-rhs",
  TABLE = "emu-table",
  TERM = "emu-t",
  VALUE = "emu-val",

  ANCHOR = "a",
  BOLD = "b",
  CODE = "code",
  HEADER = "h1",
  ITALIC = "i",
  LIST_ITEM = "li",
  ORDERED_LIST = "ol",
  PARAGRAPH = "p",
  PREFORMAT = "pre",
  SUBSCRIPT = "sub",
  SUPERSCRIPT = "sup",
  TABLE_HEAD = "th",
  TABLE_ROW = "tr",
  UNORDERED_LIST = "ul",
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
