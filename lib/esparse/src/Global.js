// global util
// const inRange = (pos, loc) => {
//   const {start, end} = loc;
//   return (start.line <= pos.line && pos.line <= end.line) &&
//     (start.column <= pos.column && pos.column <= end.column)
// }
const popIdx = (idx, arr) => {
  return arr.slice(0, idx).concat(arr.slice(idx + 1, arr.length));
}

// save original source
var _src = "";
const initSrc = (src) => _src = src;
const sliceSrc = (start, end) => {
  return _src.slice(start, end);
}


// Handle trailing comma
var _commas = [];
const initComma = () => _commas = [];
const addComma = pos => _commas.push(pos);
const checkComma = (start, end, lastChar) => {
  const code = sliceSrc(start, end);
  
  // refine ranges by lastChar
  let pass = true;
  let offset = 0;
  for (let i = code.length - 1; i >= 0 ; i -= 1) {
    if (code[i] === lastChar) {
      if (pass) pass = false;
      else { offset = i; break; }
    }
  }
  start += offset;

  for (let i = 0 ; i < _commas.length ; i += 1) {
    let commaPos = _commas[i];
    if (start <= commaPos && commaPos < end) {
      _commas = popIdx(i, _commas);
      return true;
    }
  }
  return false;
}

// checkCoverCallExpressionAndAsyncArrowHead
const checkCoverCallExpressionAndAsyncArrowHead = (start, end) => {
  let code = sliceSrc(start, end);
  return code.indexOf("(") > -1 && code.indexOf(")") > -1
}

// init
const init = (src) => {
  initSrc(src);
  initComma();
}

module.exports = {
  init,
  checkCoverCallExpressionAndAsyncArrowHead,
  addComma,
  checkComma,
};
