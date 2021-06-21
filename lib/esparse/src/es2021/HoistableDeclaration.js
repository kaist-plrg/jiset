const Node = require('../Node');

// HoistableDeclaration[Yield, Await, Default] :
//    FunctionDeclaration[?Yield, ?Await, ?Default]
//    GeneratorDeclaration[?Yield, ?Await, ?Default]
//    AsyncFunctionDeclaration[?Yield, ?Await, ?Default]
//    AsyncGeneratorDeclaration[?Yield, ?Await, ?Default]
let HoistableDeclaration = (Yield, Await, Default) => (given) => {
  const FunctionDeclaration = require('./FunctionDeclaration');
  const GeneratorDeclaration = require('./GeneratorDeclaration');
  const AsyncFunctionDeclaration = require('./AsyncFunctionDeclaration');
  const AsyncGeneratorDeclaration = require('./AsyncGeneratorDeclaration');
  let params = [Yield, Await, Default];
  let { async, generator } = given;
  let index, func;
  if (!async && !generator) {
    index = 0;
    func = FunctionDeclaration(Yield, Await, Default)(given);
  } else if (!async && generator) {
    index = 1;
    func = GeneratorDeclaration(Yield, Await, Default)(given);
  } else if (async && !generator) {
    index = 2;
    func = AsyncFunctionDeclaration(Yield, Await, Default)(given);
  } else {
    index = 3;
    func = AsyncGeneratorDeclaration(Yield, Await, Default)(given);
  }
  return new Node('HoistableDeclaration', given, index, [func], params);
}

module.exports = HoistableDeclaration;
