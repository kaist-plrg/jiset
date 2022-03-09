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
