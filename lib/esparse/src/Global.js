// util
const inRange = (pos, loc) => {
  const {start, end} = loc;
  return (start.line <= pos.line && pos.line <= end.line) &&
    (start.column <= pos.column && pos.column <= end.column)
}
const popIdx = (idx, arr) => {
  return arr.slice(0, idx).concat(arr.slice(idx + 1, arr.length));
}

// Handle trailing comma
var _commas = [];
const initComma = () => _commas = [];
const addComma = loc => _commas.push(loc);
const checkComma = loc => {
  for (let i = 0 ; i < _commas.length ; i += 1) {
    if(inRange(_commas[i], loc)) {
      _commas = popIdx(i, _commas);
      return true;
    }
  }
  return false;
}

module.exports = {
  initComma,
  addComma,
  checkComma,
};
