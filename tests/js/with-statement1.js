var outerScope = {x: 0};
var innerScope = {x: 1};
var noerror = 0;

with (outerScope) {
  with (innerScope) {
    x = (delete innerScope.x, 2);
  }
}

if (innerScope.x !== 2) {
    noerror = 1;
}
if (outerScope.x !== 0) {
    noerror = 2;
}
