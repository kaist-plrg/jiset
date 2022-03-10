module.exports = {
  target: ["web", "es5"],
  mode: "development",
  entry: "./input.temp.js",
  output: {
    path: __dirname,
    filename: "./output.temp.js",
  },
  module: {
    rules: [
      {
        use: {
          loader: "babel-loader",
          options: {
            exclude: [
              /node_modules[\\\/]core-js/,
              /node_modules[\\\/]webpack[\\\/]buildin/,
              /node_modules[\\\/]regenerator-runtime/,
            ],
            presets: [
              [
                "@babel/preset-env",
                {
                  spec: true,
                  useBuiltIns: "usage",
                  corejs: "3.21.1",
                },
              ],
            ],
          },
        },
      },
    ],
  },
  devtool: "source-map",
};
