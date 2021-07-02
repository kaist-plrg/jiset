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
    default: 'total',
    nargs: 1,
    })
  .option('mimic', {
    alias: 'm',
    describe: '',
    type: 'boolean',
    default: false,
    nargs: 0,
  })  
  .alias('help', 'h')
  .version('1.0.0')
  .alias('version', 'v')
  .argv;
let target = argv.target;
let mimic = argv.mimic;

module.exports = { target, mimic };
