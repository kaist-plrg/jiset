const LexicalNode = require('./LexicalNode');

class Node {
  constructor(kind, elem, index, children, params) {
    if (children === undefined) children = [];
    if (params === undefined) params = [];
    if (elem?.loc) this.loc = elem.loc;
    if (elem?.start >= 0) this.start = elem.start;
    if (elem?.end) this.end = elem.end;
    this.kind = kind;
    this.index = index;
    this.children = children;
    this.params = params;
  }

  compress() {
    let root = [];
    let queue = [{ parent: root, target: this}];

    while (queue.length > 0) {
      let { parent, target } = queue.shift();
      if (target instanceof LexicalNode)
        parent.push(target.compress());
      else if (target instanceof Node) {
        let arr = [];
        let result = [
          target.index,
          arr,
          target.params.map(Number),
          target.compressedLoc(),
        ];
        parent.push(result);
        target.children.forEach(child => {
          queue.push({ parent: arr, target: child });
        });
      } else {
        parent.push(target);
      }
    }

    return root[0];
  }

  compressedLoc() {
    if (!this.loc) return [-1, -1, -1, -1, -1, -1];
    let { start, end } = this.loc;
    return [start.line, start.column, this.start, end.line, end.column, this.end];
  }
}
Node.fromList = (kind, elems, genChild, params) => {
  let list;
  for (let elem of elems) {
    let child = genChild(elem);
    if (list === undefined) {
      list = new Node(kind, elem, 0, [child], params);
    } else {
      let loc0 = list.loc;
      let start0 = list.start;
      list = new Node(kind, elem, 1, [list, child], params);
      if (list.loc !== undefined) {
        list.loc.start = loc0.start;
      }
      list.start = start0;
      list.end = child.end;
    }
  }
  return list;
}
Node.getRhs = (nameList, genList, given) => {
  let index = nameList.indexOf(given.type);
  if (index === -1) return null;
  let child = genList[index](given);
  return { index, child };
}
Node.TODO = (msg) => {
  throw `[TODO] ${msg}`
};

Node.getBinary = (given, ops, genLeft, genRight) => {
  let { operator, left, right } = given;
  let index = ops.indexOf(operator) + 1;
  let info = { index };
  if (index > 0) {
    let l = genLeft(left);
    let r = genRight(right);
    info.children = [l, r];
  } else {
    let expr = genRight(given);
    info.children = [expr];
  }
  return info;
}

module.exports = Node
