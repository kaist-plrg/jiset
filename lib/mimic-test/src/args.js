const yargs = require('yargs');
const argv = yargs
  .scriptName('mimic-test')
  .usage('Usage: $0')
  .example(
    '$0',
  )
  .option('target', {
    alias: 't',
    describe: 'The target built-in function name',
    type: 'string',
    nargs: 1,
    })
  .alias('help', 'h')
  .version('1.0.0')
  .alias('version', 'v')
  .argv;
let target = argv.target;

module.exports = { target };
