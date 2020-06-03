import cheerio from "cheerio";
import path from "path";
import { Spec } from "./spec";
import { copy, printSep, loadSpec, saveFile } from "./util";
import { getESVersion, getDir, loadRule } from "./util";
import { ECMAScriptVersion } from "./enum";
import { Grammar } from "./grammar";

const argv = require('yargs')
  .usage("Usage: --target <ECMAScript-version>")
  .option('target', {
    alias: 't',
    default: 'es10',
    describe: 'the version of ECMAScript',
    type: 'string',
  })
  .help()
  .alias('help', 'h')
  .argv;

async function main() {
  try {
    const version = getESVersion(argv.target);
    const lowerVersion = version.toLowerCase();
    printSep();
    console.log(`VERSION: ${version}`);
    const resourcePath = path.join(__dirname, "..", "resource");
    const html = await loadSpec(resourcePath, version);
    const rule = await loadRule(resourcePath, version);
    let $ = cheerio.load(html);

    // extract Spec from a ECMAScript html file
    const spec = Spec.from($, rule);

    // save algorithms in JSON format files
    printSep();
    console.log("saving file...");
    const basePath = getDir(__dirname, "..", "..", "..")
    const specDirPath = getDir(basePath, "src", "main", "resources", lowerVersion, "auto");
    const algoDirPath = getDir(specDirPath, "algorithm");
    for (const name in spec.algoMap) {
      const algo = spec.algoMap[name];
      const jsonPath = path.join(algoDirPath, `${name}.json`);
      const data = {
        kind: algo.head.kind,
        params: algo.head.params,
        steps: algo.steps,
        length: algo.head.length,
        filename: `${lowerVersion}/algorithm/${name}.json`
      };
      saveFile(jsonPath, data);
    }

    // serialize specification
    spec.serialize();

    // save the information as a JSON file
    const jsonPath = path.join(specDirPath , "spec.json");
    saveFile(jsonPath, spec);
    console.log("completed!!!")

  } catch (err) {
    // show error messages
    console.log(`[ERROR] ${err.message}`);
    throw err;
  }
}

main();
