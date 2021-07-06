// Parsers
const jiset = require("./lib/jiset.js");
const acorn = require("acorn");
// jiset.parseJS("1 + 1");

// node
const fs = require("fs");
const csvWriter = require('csv-writer').createObjectCsvWriter;

// tests
let test_dir = `${process.env.JISET_HOME}/tests/test262/test/`;

const targets = fs.readFileSync(`${process.env.JISET_HOME}/lib/debugger/tests.json`, 'utf-8');
const targetsJSON = JSON.parse(targets);
const normal_targets = targetsJSON.normal;

// for results
let results = [];
let parseFail = [];

// MAIN : Test Parsing
normal_targets.forEach(target => {
    let filename = test_dir + target.name;
    let file = fs.readFileSync(filename, 'utf-8');

    let startA = new Date();
    acorn.parse(file, {ecmaVersion: 2021});
    let endA = new Date();
    let timeA = endA.getTime() - startA.getTime();

    let startJ = new Date();
    try {
        jiset.parseJS(file);   
        let endJ = new Date();
        let timeJ = endJ.getTime() - startJ.getTime();

        let result = {
            filename,
            filesize: fs.statSync(filename).size,
            filelines: file.split('\n').length,
            timeA,
            timeJ
        };
        results.push(result);
    } catch {
        console.log("################################");
        console.log('JS Parser error: ' + filename);
        console.log("################################");
        parseFail.push({filename, parser: "JSparser"});
    };
    
});

let writeCSV = csvWriter({
    path: "./result.csv",
    header: [
        {id: 'filename', title: 'File Name'},
        {id: 'filesize', title: 'File Size'},
        {id: 'filelines', title: 'Number of Lines of Code'},
        {id: 'timeA', title: 'Acorn Parsing Time (ms)'},
        {id: 'timeJ', title: 'JSParser Parsing Time (ms)'}
    ]
});

writeCSV
    .writeRecords(results)
    .then(() => console.log('Done'));

// Fail on parsing
let failCSV = csvWriter({
    path: "./failtests.csv",
    header: [
        {id: 'filename', title: 'File Name'},
        {id: 'parser', title: 'Parser Type'}
    ]
});

failCSV
    .writeRecords(parseFail)
    .then(() => console.log('Done2'));