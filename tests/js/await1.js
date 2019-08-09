var pi;
async function f() { a = await new Promise((resolve) => {resolve(2)}); pi = a; return a; }
var i = f();
