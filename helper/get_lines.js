// get section
function getSection(name) {
  return document.getElementById(name);
}

// get all algorithms
function getAllAlgo(section) {
  return Array.from(section.getElementsByTagName('EMU-ALG'));
}

// check alphanumeric charcter
function isAlphanum(ch) {
  return /[a-zA-Z0-9]/.test(ch)
}

// get lines
function getLines(ol) {
  let lines = [];
  for (li of ol.children) {
    let blocks = Array.from(li.childNodes);
    let tokens = [];
    for (block of blocks) {
      let text = block.innerText;
      switch (block.nodeName) {
        case 'CODE': tokens.push(`code:${text}`); break;
        case 'EMU-CONST': tokens.push(`const:${text}`); break;
        case 'EMU-VAL': tokens.push(`value:${text}`); break;
        case 'OL': tokens.push('line-list'); lines = lines.concat(getLines(block)); break;
        case 'VAR': tokens.push(`id:${text}`); break;
        default:
          if (block.nodeName == '#text') text = block.textContent;
          for (str of text.split(/\s+/)) {
            let token = '';
            for (let i = 0; i < str.length; i++) {
              if (isAlphanum(str[i])) token += str[i];
              else {
                if (token != '') tokens.push(token);
                tokens.push(str[i]);
                token = '';
              }
            }
            if (token != '') tokens.push(token);
          }
      }
    }
    lines.push(tokens.join(' '));
  }
  return lines;
}

// get lines
function getTokens(line) {
  let blocks = Array.from(line.childNodes);
}

// download given data into a file
function save(data, filename) {
  if (data === undefined) {
    console.error('No data');
    return;
  }

  var blob = new Blob([data], {type: 'text/json'});
  e = document.createEvent('MouseEvents');
  a = document.createElement('a');

  a.download = filename;
  a.href = window.URL.createObjectURL(blob);
  a.dataset.downloadurl = ['text/json', a.download, a.href].join(':');
  e.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
  a.dispatchEvent(e);
}

code = (() => {
  let lines = [];
  let section = getSection('sec-executable-code-and-execution-contexts');
  let algos = getAllAlgo(section)
  for (algo of algos) {
    lines = lines.concat(getLines(algo.children[0]));
  }
  return lines.join('\n');
})();
save(code, 'lines')
