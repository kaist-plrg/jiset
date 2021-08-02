class LexicalNode {
  constructor(kind, str) {
    this.kind = kind;
    this.str = str;
  }
  
  compress() {
    return [this.kind, this.str];
  }
}

module.exports = LexicalNode;
