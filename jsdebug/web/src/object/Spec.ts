import { Algo } from "./Algo";

export interface Spec {
  version: string;
  grammar: unknown;
  algos: Algo[];
  consts: string[];
  intrinsics: string[];
  symbols: string[];
  aoids: string[];
  section: unknown;
}

export function getAlgo(
  spec: Spec | undefined,
  algoName: string
): Algo | undefined {
  if (spec === undefined) return undefined;
  let algos = spec.algos.filter((algo) => algo.head.name === algoName);
  return algos.length === 1 ? algos[0] : undefined;
}
