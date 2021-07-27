const path = require('path');

module.exports = {
  mode: 'production',
  entry: './src/Translator.js',
  devtool: 'inline-source-map',
  output: {
    path: path.resolve(__dirname, 'build'),
    filename: 'esparse.js',
    library: 'ESParse',
    libraryTarget: 'var'
  },
};
