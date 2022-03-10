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
            exclude: /node_modules/,
            presets: [
              [
                "@babel/preset-env",
                {
                  spec: true,
                  useBuiltIns: "usage",
                  corejs: "3.21.1",
                  modules: false,
                },
              ],
            ],
            overrides: [{
              sourceType: "script",
            }],
          },
        },
      },
    ],
  },
  devtool: "source-map",
};
