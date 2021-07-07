var jiset = require("./lib/jiset.js");
var fs = require("fs");
var specRaw = fs.readFileSync("./spec.json", "utf-8");
var jsSource = "var x = 1 + 2; print(x);";

jiset.setSpec(specRaw);
var jsScript = jiset.parseJS(jsSource);
jiset.eval(jsScript);
