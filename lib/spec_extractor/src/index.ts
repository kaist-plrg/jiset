import cheerio from "cheerio";
import path from "path";
import { getDir, loadRule, loadSpec, saveGrammarResult, saveFile } from "./util";
import { ECMAScriptVersion } from "./enum";
import { GrammarExtractResult } from "./types";
import { extractSection, generateIdxMap } from "./grammar";
import { extractAlgoClause } from "./algorithm";

async function main () {
  const version = ECMAScriptVersion.ES2019;
  const resourcePath = path.join( __dirname, "..", "resource" );
  const html = await loadSpec( resourcePath, version );
  const rule = await loadRule( resourcePath, version );
  let $ = cheerio.load( html );

  console.log( rule );

  /**
   * Grammar
   */
  // let grammar: GrammarExtractResult = {
  //   lexProds: [],
  //   prods: []
  // };

  // /* extract grammar production */
  // rule.grammar.sections.forEach( section => {
  //   let sectionResult = extractSection( { $, rule, section } );
  //   const { lexProds, prods } = sectionResult;
  //   grammar.lexProds = grammar.lexProds.concat( lexProds );
  //   grammar.prods = grammar.prods.concat( prods );
  // } );

  // /* generate index map */
  // grammar.idxMap = generateIdxMap( grammar );

  // /* save grammar extract result */
  // saveGrammarResult( resourcePath, version, grammar );

  /**
   * Algorithm
   */

  // getAlgoHeader( "await", { $ } );
  const clause = extractAlgoClause( { $, clauseId: "sec-getidentifierreference" } );

  saveFile( path.join( getDir(resourcePath, '.cache'), 'spec.json' ), JSON.stringify( clause.body ) );
}

try {
  main();
} catch ( err ) {
  throw err;
}
