// algorithm object
export interface Algo {
  head: AlgoHeader;
  id: string;
  rawBody: string;
  code: string[];
}

// parameter
export interface Param {
  name: string;
  kind: string;
}

// algorithm header
export type AlgoHeader =
  | NormalHeader
  | MethodHeader
  | SyntaxDirectedHeader
  | BuiltinHeader;

export interface NormalHeader {
  name: string;
  params: Param[];
}
export interface MethodHeader {
  base: string;
  methodName: string;
  receiverParam: Param;
  origParams: Param[];
}
export interface SyntaxDirectedHeader {
  lhsName: string;
  idx: number;
  subIdx: number;
  methodName: string;
  rhsParams: Param[];
  isStatic: boolean;
  withParams: Param[];
  needPrefix: boolean;
}
export interface BuiltinHeader {
  ref: string;
  origParams: Param[];
}
enum AlgoHeaderType {
  NORMAL,
  METHOD,
  SYNTAX,
  BUILTIN,
}

// helper functions
function getHeaderType ( algo: Algo ): AlgoHeaderType {
  const { head } = algo;
  if ( "name" in head ) return AlgoHeaderType.NORMAL;
  else if ( "base" in head ) return AlgoHeaderType.METHOD;
  else if ( "lhsName" in head ) return AlgoHeaderType.SYNTAX;
  else return AlgoHeaderType.BUILTIN;
}
export function getName ( algo: Algo ): string {
  switch ( getHeaderType( algo ) ) {
    case AlgoHeaderType.NORMAL:
      return getNormalName( algo.head as NormalHeader );
    case AlgoHeaderType.METHOD:
      return getMethodName( algo.head as MethodHeader );
    case AlgoHeaderType.SYNTAX:
      return getSyntaxName( algo.head as SyntaxDirectedHeader );
    case AlgoHeaderType.BUILTIN:
      return getBuiltinName( algo.head as BuiltinHeader );
  }
}
export function getParams ( algo: Algo ): Param[] {
  switch ( getHeaderType( algo ) ) {
    case AlgoHeaderType.NORMAL:
      return getNormalParams( algo.head as NormalHeader );
    case AlgoHeaderType.METHOD:
      return getMethodParams( algo.head as MethodHeader );
    case AlgoHeaderType.SYNTAX:
      return getSyntaxParams( algo.head as SyntaxDirectedHeader );
    case AlgoHeaderType.BUILTIN:
      return getBuiltinParams();
  }
}
export function getHeaderStr ( algo: Algo ): string {
  const name = getName( algo );
  const params = getParams( algo );
  const paramsStr = params
    .map( ( p ) => {
      if ( p.kind === "Variadic" ) return "..." + p.name;
      else if ( p.kind === "Optional" ) return "?" + p.name;
      else return p.name;
    } )
    .join( ", " );
  return `${ name } [${ paramsStr }]`;
}

// normal header
export function getNormalName ( head: NormalHeader ): string {
  return head.name;
}
export function getNormalParams ( head: NormalHeader ): Param[] {
  return head.params;
}

// method header
export function getMethodName ( head: MethodHeader ): string {
  return head.base + "." + head.methodName;
}
export function getMethodParams ( head: MethodHeader ): Param[] {
  return [ head.receiverParam ].concat( head.origParams );
}

// syntax header
const THIS_PARAM = { name: "this", kind: "Normal" };
const ARGS_LIST = { name: "argumentsList", kind: "Normal" };
const NEW_TARGET = { name: "NewTarget", kind: "Normal" };
export function getSyntaxName ( head: SyntaxDirectedHeader ): string {
  const { lhsName, idx, subIdx, methodName } = head;
  return `${ lhsName }[${ idx },${ subIdx }].${ methodName }`;
}
export function getSyntaxParams ( head: SyntaxDirectedHeader ): Param[] {
  return [ THIS_PARAM ].concat( head.rhsParams ).concat( head.withParams );
}

// builtin header
export function getBuiltinName ( head: BuiltinHeader ): string {
  const { ref } = head;
  return `GLOBAL.${ ref }`;
}
export function getBuiltinParams (): Param[] {
  return [ THIS_PARAM, ARGS_LIST, NEW_TARGET ];
}
