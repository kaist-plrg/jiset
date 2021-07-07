const fs = require('fs');
const yargs = require('yargs')
const argv = yargs
  .scriptName('extract-trace')
  .usage('Usage: $0 [targetFile]')
  .example(
    '$0 sample.js',
    '// Parse JavaScript files in ECMAScript syntax',
  )
  .alias('help', 'h')
  .version('1.0.0')
  .alias('version', 'v')
  .argv;

let targetFile = argv._[0];
if (targetFile === undefined || !targetFile.endsWith(".json")) {
  console.error('Please insert a target file in a JSON format.');
  yargs.showHelp();
  process.exit(1);
}
if (!fs.existsSync(targetFile)) {
  console.log(`File not found: ${targetFile}`);
  process.exit(1);
}
const content = fs.readFileSync(targetFile, 'utf-8');

let target;
try {
  target = JSON.parse(content)
} catch {
  console.log(`Invalid JSON format: ${targetFile}`);
  process.exit(1);
}

module.exports = { target };
