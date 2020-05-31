import fs from "fs";
import path from "path";
import { get } from "request-promise";
import { ECMAScriptVersion, HTMLTagAttribute } from "./enum";
import { ExtractorRule } from "./rule";

const fsOption = { encoding: "utf8" };

export const printSep = () => {
  console.log("==========================================================");
}

const getVersionNumber = (version: ECMAScriptVersion) => {
  switch (version) {
    case ECMAScriptVersion.ES2020: return "11.0";
    case ECMAScriptVersion.ES2019: return "10.0";
    case ECMAScriptVersion.ES2018: return "9.0";
    case ECMAScriptVersion.ES2017: return "8.0"; case ECMAScriptVersion.ES2016: return "7.0";
  }
}

const getSpecUrl = (version: ECMAScriptVersion) => {
  return `https://www.ecma-international.org/ecma-262/${ getVersionNumber(version) }/index.html`;
}

// get directory path and recursively create the non-existed directories in the path
export const getDir =
  (...args: string[]): string => {
    const dirPath = path.join.apply(path, args);
    if (!fs.existsSync(dirPath)) {
      fs.mkdirSync(dirPath);
    }
    return dirPath;
  }

// load specification
export const loadSpec = async (resourcePath: string, version: ECMAScriptVersion) => {
  printSep();
  console.log("loading spec...");

  /* check if cache exists */
  const cacheDirPath = getDir(resourcePath, ".cache");
  const cachePath = path.join(cacheDirPath, `${ version }.html`);
  const cacheExists = fs.existsSync(cachePath);
  let specContent: string;

  if (cacheExists) {
    /* if cache exists, read cached file */
    specContent = fs.readFileSync(cachePath, fsOption);
  } else {
    /* if cache doesn't exist, download spec from internet */
    const specUrl = getSpecUrl(version);
    console.log(`download ${ version } from ${ specUrl }...`);
    const HTMLContent: string = await get(specUrl);
    console.log(`completed`);
    /* save it to cache dir */
    fs.writeFileSync(cachePath, HTMLContent, fsOption);
    specContent = HTMLContent;
  }

  console.log("completed!!!");
  return specContent;
}

//load rules
export const loadRule =
  (resourcePath: string, version: ECMAScriptVersion): ExtractorRule => {
    printSep();
    console.log("loading rules...");

    const rulePath = path.join(resourcePath, "rules", `${ version }.json`);
    const ruleExists = fs.existsSync(rulePath);

    /* assert rule file exists */
    if (!ruleExists)
      throw new Error(`loadRule: rulePath(${ rulePath }) is invalid`);

    const ruleContent = fs.readFileSync(rulePath, fsOption);
    const ruleObj = JSON.parse(ruleContent);

    console.log("completed!!!");
    return ruleObj;
  }

// save file
export const saveFile =
  (filePath: string, content: string) => {
    printSep();
    console.log("saving file...");
    fs.writeFileSync(filePath, content, fsOption);
    console.log("completed!!!")
  }

// get attributes from CheerioElement
export const getAllAttributes =
  (elem: CheerioElement): { [ attr: string ]: any } => {
    let ret: { [ attr: string ]: any } = {};
    // parse 
    for (let key in elem.attribs) {
      let val = elem.attribs[ key ];
      switch (key) {
        case HTMLTagAttribute.PARAMS: {
          ret[ key ] = val.split(",").map(_ => _.trim());
          break;
        }
        case HTMLTagAttribute.OPTIONAL: {
          ret[ key ] = true;
          break;
        }
        case HTMLTagAttribute.CONSTRAINTS: {
          switch (val[ 0 ]) {
            case "+":
              ret[ key ] = `p${ val.substr(1) }`; break;
            case "~":
              ret[ key ] = `!p${ val.substr(1) }`; break;
          }
          break;
        }
        case HTMLTagAttribute.ONEOF: {
          ret[ key ] = true;
          break;
        }
        default:
          ret[ key ] = val;
      }
    }

    // set params to array
    ret.params = ret.params ? ret.params : [];
    // set optional to boolean
    ret.optional = ret.optional ? ret.optional : false;
    // set constraints to string
    ret.constraints = ret.constraints ? ret.constraints : "";
    // set oneof to boolean
    ret.oneof = ret.oneof ? ret.oneof : false;

    return ret;
  }

// string normalization
export const norm = (str: string) => {
  return str
    .replace(/\s+/g, '')
    .replace(/\//g, '')
    .replace('#', '');
}

// spilt string
export const splitText = (str: string): string[] => {
  let tokens: string[] = [];
  let prevWordChar = false;
  for (let ch of str) {
    let isWordChar = /\w/.test(ch);
    let isSpace = /\s/.test(ch);
    if (prevWordChar && isWordChar) tokens.push(tokens.pop() + ch);
    else if (!isSpace) tokens.push(ch);
    prevWordChar = isWordChar;
  }
  return tokens;
}

// get ECMAScript Version
export const getESVersion = (target: string): ECMAScriptVersion => {
  switch (target) {
    case 'es11': return ECMAScriptVersion.ES2020;
    case 'es10': return ECMAScriptVersion.ES2019;
    case 'es9': return ECMAScriptVersion.ES2018;
    case 'es8': return ECMAScriptVersion.ES2017;
    case 'es7': return ECMAScriptVersion.ES2016;
  }
  throw new Error(`getESVersion: Invalid ECMAScript version - ${target}`);
}

// copy values
export const copy = (value: any): any => JSON.parse(JSON.stringify(value));

// unwrapping characters
export const unwrap = (
  str: string,
  size: number = 1
): string => str.substring(size, str.length - size);
