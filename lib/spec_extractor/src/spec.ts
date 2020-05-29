import { Grammar } from "./grammar";
import { ExtractorRule } from "./rule";
import assert from "assert";

export class Spec {
  constructor(
    // TODO public globalMethods: ???
    // TODO public consts: ???
    public grammar: Grammar
    // TODO public symbols: ???
    // TODO public intrinsics: ???
    // TODO public tys: ???
  ) { }

  // extract Spec from a ECMAScript html file
  static from(
    $: CheerioStatic,
    rule: ExtractorRule
  ) {
    // TODO
    const grammar = Grammar.from($, rule);
    return new Spec(grammar);
  }
}
