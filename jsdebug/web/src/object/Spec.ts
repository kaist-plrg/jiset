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
