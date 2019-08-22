var x = true;
var y = false;
try {
  eval("if(false) super();");
  x = false;
} catch(e) {
  y = e instanceof SyntaxError;
}
