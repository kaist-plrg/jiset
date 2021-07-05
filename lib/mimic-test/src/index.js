#!/usr/bin/env node
const fs = require('fs');
const { target, mimic } = require('./args');
const shell = require('shelljs');
// const { mimicModel } = require('../resources/mimic-model.js')

//No max,min,sum
let prototypes = ['every', 'filter', 'forEach', 'indexOf', 'lastIndexOf',
  'map', 'pop', 'push', 'reduce', 'reduceRight', 'shift', 'some'
]

let TEST262DIR = `${process.env.JISET_HOME}/tests/test262/test/built-ins/`;
let MIMIC_HOME = `${process.env.JISET_HOME}/lib/mimic-test/`;
let MODELDIR = `${MIMIC_HOME}resources/mimic-model/`;
let RESULTDIR = `${MIMIC_HOME}results/`;

function parse(target) {
  let tokens = target.split(".");
  return tokens;
}

// foldername
function getFolderName(dir, tokens) {
  let str = tokens.join("/");
  let foldername = `${dir}${str}`;
  return foldername;
}

// foldername of the test
function getTestFolderName(tokens) {
  return getFolderName(TEST262DIR,tokens);
}

// foldername of the resource
function getModelFolderName(tokens) {
  return getFolderName(MODELDIR,tokens);
}

// list of filenames under given folder
function getFileNames(foldername) {
  try {
    let filenames = fs.readdirSync(foldername);
    return filenames;
  } catch {
    console.error(`improper folder name : ${foldername}`);
    process.exit(1);
  }
}

// content of file
function getFileContent(foldername, filename) {
  let content = fs.readFileSync(foldername + '/' + filename, 'utf8');
  return content
}

// JSfiles included in filename's content
function getIncludedJS(foldername, filename) {
  let content = getFileContent(foldername, filename);
  let position = content.indexOf("includes:")
  if (position === -1) {
    return [];
  } else {
    let start = content.indexOf("[", position);
    let end = content.indexOf("]", position);
    if (start === -1 || end === -1) {
      return [];
    } else {
      let includedFileNames = content.substring(start + 1, end).split(",");
      return includedFileNames
    }
  }
}

// All contents of JSfiles' included in filenames' contents
function getAllIncludedJS(foldername, filenames) {
  // Default binding
  let JSfilenames = ['assert.js', 'sta.js'];
  filenames.forEach(filename =>
    JSfilenames = JSfilenames.concat(getIncludedJS(foldername, filename)));
  //Deduplication
  JSfilenames = JSfilenames.filter((item, pos) => JSfilenames.indexOf(item) === pos);
  let harnessfoldername = `${process.env.JISET_HOME}/tests/test262/harness/`;
  let includedcontents = "";
  JSfilenames.forEach(filename =>
    includedcontents += getFileContent(harnessfoldername, filename))
  return includedcontents
}

function makeMergedFile(foldername, filename, includedcontents) {
  let mergedcontent = includedcontents + getFileContent(foldername, filename);
  try {
    fs.writeFileSync(`${MIMIC_HOME}.merged.js`, mergedcontent, 'utf8');
  } catch (err) {
    console.error(`write fail!`)
    process.exit(1);
  }
}

function test(target, mimic) {
  let originOrMimic = 'original';
  if (mimic) {
    originOrMimic = 'mimic';
  }
  let tokens = parse(target);
  let foldername = getTestFolderName(tokens);
  let filenames = getFileNames(foldername);
  let includedcontents = getAllIncludedJS(foldername, filenames);
  let success_array = [];
  let fail_array = [];
  let log_array = [];
  if (mimic) {
    let modelfoldername = getModelFolderName(tokens);
    let modelfilename = "mimic.js";
    includedcontents = getFileContent(modelfoldername, modelfilename) + includedcontents;
  }
  // make results dir
  shell.cd(`${RESULTDIR}`);
  let resultfolder = RESULTDIR+target
  shell.rm('-rf', `${resultfolder}/${originOrMimic}`);
  shell.mkdir('-p',`${resultfolder}/${originOrMimic}`);
  filenames.forEach(filename => {
    makeMergedFile(foldername, filename, includedcontents);
    shell.cd(`${MIMIC_HOME}`);
    let shellstr = shell.exec('timeout 1s node .merged.js', { silent : true});
    if (shellstr.code === 0) {
      success_array.push(`${foldername}/${filename}`);
      log_array.push(`${foldername}/${filename}`);
    } else {
      fail_array.push(`${foldername}/${filename}`);
      log_array.push(`${foldername}/${filename}`);
      log_array.push(shellstr.stderr);
    }
  }
  );
  
  let result_array = [
    "----------------------------------------",
    `${target}`,
    "----------------------------------------",
    `Success : ${success_array.length}`,
    `Fail : ${fail_array.length}`,
    `Total : ${success_array.length + fail_array.length}`,
    "----------------------------------------"]
  //수정
  let success_str = success_array.join("\n");
  let fail_str = fail_array.join("\n");

  let log_str = log_array.join("\n");
  
  let result_str = result_array.join("\n");
  console.log(result_str);
  //

  try {
    fs.writeFileSync(`${resultfolder}/${originOrMimic}/success.log`, success_str, 'utf8');
    fs.writeFileSync(`${resultfolder}/${originOrMimic}/fail.log`, fail_str, 'utf8');
    fs.writeFileSync(`${resultfolder}/${originOrMimic}/log.log`, log_str, 'utf8');
    fs.writeFileSync(`${resultfolder}/${originOrMimic}/result.log`, result_str, 'utf8');
  } catch (err) {
    console.error(`write fail! ${target}`)
    process.exit(1);
  }
  return { originOrMimic, success_array, fail_array, success_str, fail_str, log_str, result_str };
}

function totalTest(mimic) {
  //original or mimic
  let every_originOrMimic = '';
  //count variable
  let every_success_count = 0;
  let every_fail_count = 0;
  //log string variable
  let every_success_str = '';
  let every_fail_str = '';
  let every_log_str = '';
  let every_result_str = '';
  
  prototypes.forEach(proto => {
    let eachtarget = `Array.prototype.${proto}`;
    let each = test(eachtarget, mimic);
    every_originOrMimic = each.originOrMimic;
    every_success_count += each.success_array.length;
    every_fail_count += each.fail_array.length;
    every_success_str += each.success_str;
    every_fail_str += each.fail_str;
    every_log_str += each.log_str;
    every_result_str += each.result_str;
  }
  );

  // make results dir
  shell.cd(`${RESULTDIR}`);
  let resultfolder = RESULTDIR+'Total'
  shell.rm('-rf', `${resultfolder}/${every_originOrMimic}`);
  shell.mkdir('-p',`${resultfolder}/${every_originOrMimic}`);

  let every_result_array = [
    "========================================",
    'Total',
    "========================================",
    `Success : ${every_success_count}`,
    `Fail : ${every_fail_count}`,
    `Total : ${every_success_count + every_fail_count}`,
    "========================================"];
  let result_str = every_result_array.join("\n");
  console.log(result_str);
  every_result_str+= result_str;

  try {
    fs.writeFileSync(`${resultfolder}/${every_originOrMimic}/success.log`, every_success_str, 'utf8');
    fs.writeFileSync(`${resultfolder}/${every_originOrMimic}/fail.log`, every_fail_str, 'utf8');
    fs.writeFileSync(`${resultfolder}/${every_originOrMimic}/log.log`, every_log_str, 'utf8');
    fs.writeFileSync(`${resultfolder}/${every_originOrMimic}/result.log`, every_result_str, 'utf8');
  } catch (err) {
    console.error(`write fail! ${target}`)
    process.exit(1);
  }
}

if (target === 'total') {
  totalTest(mimic);
} else {
  test(target, mimic);
}
