const Node = require('../Node');

// MethodDefinition[Yield, Await] :
//    PropertyName[?Yield, ?Await] ( UniqueFormalParameters[~Yield, ~Await] ) { FunctionBody[~Yield, ~Await] }
//    GeneratorMethod[?Yield, ?Await]
//    AsyncMethod[?Yield, ?Await]
//    AsyncGeneratorMethod[?Yield, ?Await]
//    get PropertyName[?Yield, ?Await] ( ) { FunctionBody[~Yield, ~Await] }
//    set PropertyName[?Yield, ?Await] ( PropertySetParameterList ) { FunctionBody[~Yield, ~Await] }
let MethodDefinition = (Yield, Await) => (given) => {
  const PropertyName = require('./PropertyName');
  const UniqueFormalParameters = require('./UniqueFormalParameters');
  const GeneratorMethod = require('./GeneratorMethod');
  const AsyncMethod = require('./AsyncMethod');
  const AsyncGeneratorMethod = require('./AsyncGeneratorMethod');
  const FunctionBody = require('./FunctionBody');
  const PropertySetParameterList = require('./PropertySetParameterList');

  let params = [Yield, Await];
  let { kind, key, value, generator, async } = given;
  switch (kind) {
    case 'method': {
      if (!async && !generator) {
        let x = PropertyName(Yield, Await)(given);
        let ps = UniqueFormalParameters(false, false)(value.params);
        let b = FunctionBody(false, false)(value.body);
        return new Node('MethodDefinition', given, 0, [x, ps, b], params);
      } else if (!async && generator) {
        let method = GeneratorMethod(Yield, Await)(given);
        return new Node('MethodDefinition', given, 1, [method], params);
      } else if (async && !generator) {
        let method = AsyncMethod(Yield, Await)(given);
        return new Node('MethodDefinition', given, 2, [method], params);
      } else {
        let method = AsyncGeneratorMethod(Yield, Await)(given);
        return new Node('MethodDefinition', given, 3, [method], params);
      }
    }
    case 'get': {
      let name = PropertyName(Yield, Await)({key, loc: key.loc});
      let body = FunctionBody(false, false)(value.body);
      return new Node('MethodDefinition', given, 4, [name, body], params);
    }
    case 'set': {
      let name = PropertyName(Yield, Await)({key, loc: key.loc});
      let ps = PropertySetParameterList()(value.params[0]);
      let body = FunctionBody(false, false)(value.body);
      return new Node('MethodDefinition', given, 5, [name, ps, body], params);
    }
    default:
      Node.TODO(`${kind} @ MethodDefinition`);
  }
}

module.exports = MethodDefinition;
