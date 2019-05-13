// get section
function getSection(name) {
  return document.getElementById(name);
}

// get all algorithms
function getAllAlgo(section) {
  return Array.from(section.getElementsByTagName('EMU-ALG'));
}

// get steps
function getStep(li, pre) {
  let blocks = Array.from(li.childNodes);
  let tokens = [];
  for (block of blocks) {
    let text = block.innerText;
    switch (block.nodeName) {
      case 'CODE':
      case 'EMU-CONST':
      case 'EMU-VAL': tokens.push('<value>' + text + '</value>'); break;
      case 'VAR': tokens.push('<id>' + text + '</id>'); break;
      case 'OL': tokens.push(getStepList(block, pre)); break;
      case '#text': tokens.push(block.textContent.trim()); break;
      default: tokens.push(text.trim()); break;
    }
  }
  return pre + '<step>' + tokens.join(' ') + '</step>\n';
}

// get step lists
function getStepList(ol, pre) {
  let code = '';
  code += pre + '<step-list>' +'\n';
  for (li of ol.children) {
    code += getStep(li, pre + '  ');
  }
  code += pre + '</step-list>';
  return code;
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

function getHead(algo) {
  let heads = algo.parentElement.getElementsByTagName('H1');
  if (heads.length == 0) { console.warn('no H1', algo); return undefined; }
  let head = heads[0];

  let secnoElem = head.childNodes[0];
  let secno = secnoElem.innerText
  if (secno.startsWith('B')) return undefined;

  let name = head.innerText;

  return {
    secno: secno,
    name: name
  };
}

function getAlgo(head, algo) {
  let code = '';
  code += '<algorithm>' + '\n';
  code += '  <name>' + head.name + '</name>' + '\n';
  code += getStepList(algo.children[0], '  ') + '\n';
  code += '</algorithm>';
  return code;
}

code = (() => {
  let algos = {};
  for (algo of getAllAlgo(document)) {
    let head = getHead(algo);
    if (head) {
      if (algos[head.secno] === undefined) algos[head.secno] = [];
      algos[head.secno].push(getAlgo(head, algo));
    }
  }
  console.log(algos);
})();
