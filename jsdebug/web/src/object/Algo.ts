// Algorithm object
export interface Algo {
  head: AlgoHeader;
  id: string;
  rawBody: string;
  code: string[];
}

export interface AlgoHeader {
  name: string;
  params: Param[];
}

export interface Param {
  name: string;
  kind: "Normal" | "Optional" | "Variadic";
}

export function getName ( algo: Algo ): string {
  const { head } = algo;
  return (
    head.name +
    " [" +
    head.params
      .map( ( p ) => {
        if ( p.kind === "Variadic" ) return "..." + p.name;
        else if ( p.kind === "Optional" ) return "?" + p.name;
        else return p.name;
      } )
      .join( ", " ) +
    "]"
  );
}
