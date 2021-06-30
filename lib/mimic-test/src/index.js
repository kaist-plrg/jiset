#!/usr/bin/env node
const fs = require('fs');
const { target } = require('./args');
const shell = require('shelljs');

function parse(target) {
	let tokens = target.split(".");
	return tokens;
}

function getFolderName(tokens) {
	let str = tokens.join("/");
  let foldername = `${process.env.JISET_HOME}/tests/test262/test/built-ins/${str}`;
  return foldername;
}

function getFileNames(foldername) {
	try {
		let filenames =  fs.readdirSync(foldername);
		return filenames;
	} catch {
		console.error(`improper folder name : ${foldername}`);
		process.exit(1);
	}
}

// content of file
function getFileContent(foldername,filename) {
	let content = fs.readFileSync(foldername+'/'+filename,'utf8');
	return content
}

// JSfiles included in filename's content
function getIncludedJS(foldername,filename) {
	let content = getFileContent(foldername,filename);
	let position = content.indexOf("includes:")
	if (position === -1) {
		return [];
	} else {
		let start = content.indexOf("[",position);
		let end = content.indexOf("]",position);
		if (start === -1 || end === -1) {
			return [];
		} else {
			let includedFileNames = content.substring(start+1,end).split(",");
			return includedFileNames
		}
	}
}

// All contents of JSfiles' included in filenames' contents
function getAllIncludedJS(foldername, filenames) {
	// Default binding
	let JSfilenames = ['assert.js','sta.js'];
	filenames.forEach(filename => 
		JSfilenames = JSfilenames.concat(getIncludedJS(foldername,filename)));
		//Deduplication
	JSfilenames = JSfilenames.filter((item,pos) => JSfilenames.indexOf(item) === pos);
	let harnessfoldername = `${process.env.JISET_HOME}/tests/test262/harness/`;
	let includedcontents = "";
	JSfilenames.forEach(filename=>
		includedcontents +=getFileContent(harnessfoldername,filename))
	return includedcontents
}

function makeMergedFile(foldername,filename,includedcontents) {
	let mergedcontent = includedcontents + getFileContent(foldername,filename);
	try {
		fs.writeFileSync(`${process.env.JISET_HOME}/.merged.js`, mergedcontent, 'utf8');
	} catch (err) {
		console.error(`write fail!`)
		process.exit(1);
	}
}



let tokens = parse(target);
let foldername = getFolderName(tokens);
let filenames = getFileNames(foldername);
let includedcontents = getAllIncludedJS(foldername,filenames);
let success_count = 0;
let fail_count = 0;
filenames.forEach(filename=>{
	makeMergedFile(foldername,filename,includedcontents);
	shell.cd(`${process.env.JISET_HOME}`);
	let shellstr = shell.exec('node .merged.js');
	if (shellstr.code === 0) {
		success_count += 1;
		console.log("PASS");
	} else {
		fail_count += 1;
		console.log(`Fail: ${filename}`);
		console.log(shellstr.stderr);
	}
	}
	);
let total_count = success_count + fail_count;
console.log(`Success : ${success_count}`);
console.log(`Fail : ${fail_count}`);
console.log(`Total : ${total_count}`);