# Target Errors

## [#2242](https://github.com/tc39/ecma262/pull/2242/files): Editorial: put underscores around alias name

- **Version**: [31f3c2b0c220cfea849a99c4f4ef22d93ddac14e](https://github.com/tc39/ecma262/commits/31f3c2b0c220cfea849a99c4f4ef22d93ddac14e)
  - **Type**: Cannot Detect
  - **Algorithm**: IntegerIndexedExoticObject.OwnPropertyKeys
  - **Description**: 둘 다 동일한 IR로 컴파일 됨

## [#2238](https://github.com/tc39/ecma262/pull/2238/files): Editorial: fix number types in Array.prototype.findIndex / RegExp.prototype[Symbol.search] / {Map,Set}.prototype.size

- **Version**: [3db2f0d7d48247c1cac23d21de1ef98ac352e09c](https://github.com/tc39/ecma262/commits/3db2f0d7d48247c1cac23d21de1ef98ac352e09c)
  - **Type**: Cannot Detect
  - **Algorithm**: RegExp.prototype.@@search, Array.prototype.findIndex, Map.prototype.size, Set.prototype.size
  - **Description**: 둘 다 동일한 IR로 컴파일 됨 (mathematical value, numeric value 에 대한 자세한 분류가 필요함)

## [#2220](https://github.com/tc39/ecma262/pull/2220/files): Editorial: replace a few stray GetReferencedName invocations / re-define "super-reference"

- **Version**: [a18acef2559a6f159047ca6fc0f3dd932231fefc](https://github.com/tc39/ecma262/commits/a18acef2559a6f159047ca6fc0f3dd932231fefc)
  - **Type**: Reference Error - `GetReferencedName`
  - **Algorithm**: AssignmentExpression.Evaluation
  - **Description**: `GetReferencedName` 이라는 존재하지 않는 함수를 사용함

## [#2185](https://github.com/tc39/ecma262/pull/2185/files): Editorial: handle NaN and infinities in Math.round

- **Version**: [11c45e805c4f95e1eb81deedbda05e1bcbc08db6](https://github.com/tc39/ecma262/commits/11c45e805c4f95e1eb81deedbda05e1bcbc08db6)
  - **Type**: Cannot Detect
  - **Algorithm**: Math.round
  - **Description**: 우선 `If _n_ is an integral Number` 라는 문장이 현재 컴파일이 안되고, 컴파일이 되어도 해결하기 어려운 문제로 보임.
    - +Infinity < 0.5 라는 것이 있을 때 타입 에러가 발생해야 된다고 생각했지만, 둘 다 AbsNum 으로 표현되어 에러가 발생하지 않음
    - Math.round 에 대한 정확한 spec 이 주어지지 않은 상황에서 문제가 있음을 알기는 힘들다고 생각이 됨.

## [#2174](https://github.com/tc39/ecma262/pull/2174/files): Editorial: quick fixes re Math functions

- **Version**: [575149cfd77aebcf3a129e165bd89e14caafc31c](https://github.com/tc39/ecma262/commits/575149cfd77aebcf3a129e165bd89e14caafc31c)
  - **Type**: Cannot Detect
  - **Algorithm**: Math.round
  - **Description**: `x` 는 ESValue 로 표현되고, `x < 0.5` 를 하였을 때, 현재 분석기에서는 항상 `boolTop`을 반환함. AbsTransfer 를 수정하면 검출할 수 있을 것으로 생각됨.

## [#2121](https://github.com/tc39/ecma262/pull/2121/files): Editorial: reference correct nonterminals in IteratorDestructuringAssignmentEvaluation

- **Version**: [276af73369c33f132ec55197f82273d53eb9d89a](https://github.com/tc39/ecma262/commits/276af73369c33f132ec55197f82273d53eb9d89a)
  - **Type**: Reference Error - `AssignmentExpression`, `LeftHandSideExpression`
  - **Algorithm**: AssignmentExpression.IteratorDestructuringAssignmentEvaluation
  - **Description**: `AssignmentExpression`과 `LeftHandSideExpression`이 없는데 사용함

## [#2098](https://github.com/tc39/ecma262/pull/2098/files): Editorial: add missing ! on calls to OrdinaryObjectCreate

- **Version**: [4fa9dadbe47f5c76580bf2282b31333d0f36e3de](https://github.com/tc39/ecma262/commits/4fa9dadbe47f5c76580bf2282b31333d0f36e3de)
  - **Type**: Cannot Detect
  - **Algorithm**: Various
  - **Description**: `OrdinaryObjectCreate` 라는 함수가 애초에 abrupt completion 이 나오지 않는 상황이었다면, `!` 를 붙이는 것은 단순 assertion 을 추가하는 것이므로 버그라고 보기 힘듬.
    - 만약 `?` 를 붙여야 되는 상황이었다면, `!` 를 붙이면 안된다는 warning 을 띄울 수 있을 것 같음.

## [#1977](https://github.com/tc39/ecma262/pull/1977/files): Editorial: Use DefinePropertyOrThrow and ! prefix in GetTemplateObject

- **Version**: [5370c6cc6e35c48f1d4e46e4aff4b76d6479323b](https://github.com/tc39/ecma262/commits/5370c6cc6e35c48f1d4e46e4aff4b76d6479323b)
  - **Type**: Cannot Detect
  - **Algorithm**: `GetTemplateObject`
  - **Description**: `GetTemplateObject` 는 abrupt completion 이 나오지 않는 함수이므로, `DefineOwnProperty` 함수를 호출할 때 assertion 을 추가해 주었음.
  
## [#1976](https://github.com/tc39/ecma262/pull/1976/files): Editorial: add prefix '?' on calling ToPrimitive in Abstract Equality Comparison

- **Version**: [0b988b7700de675331ac360d164c978d6ea452ec](https://github.com/tc39/ecma262/commits/0b988b7700de675331ac360d164c978d6ea452ec)
  - **Type**: Unchecked Abrupt Completion
  - **Algorithm**: `Abstract Equality Comparison`
  - **Description**: `ToPrimitive` 는 abrupt completion 을 반환할 수 있지만, 이를 확인하지 않고 `x` 라는 값과 `==` 연산을 하고 있음.

## [#1969](https://github.com/tc39/ecma262/pull/1969/files): Editorial: Return an iterator *record* for for...in heads

- **Version**: [0e83cd040fe1f374fb472f946587d942829e755a](https://github.com/tc39/ecma262/commits/0e83cd040fe1f374fb472f946587d942829e755a)
  - **Type**: Reference Error
  - **Algorithm**: `ForIn/OfHeadEvaluation`
  - **Description**: `ForIn/OfHeadEvaluation` 은 `Iterator Record` 를 반환해야 하지만, `Iterator Object` 를 반환하는 경우가 발생했음. 이는 결국 `ForIn/OfBodyEvaluation` 의 `iteratorRecord` 로 흘러가는데, 이후 `iteratorRecord` 의 field 에 접근할 때, reference error 가 나와야함

## [#1954](https://github.com/tc39/ecma262/pull/1954/files): Editorial: Fixed typos in variable name oldvalue

- **Version**: [731fc79549be6eb6eceec457822a124ed6c35da8](https://github.com/tc39/ecma262/commits/731fc79549be6eb6eceec457822a124ed6c35da8)
  - **Type**: Reference Error
  - **Algorithm**: `UpdateExpression.Evaluation`
  - **Description**: `oldvalue` 라는 정의되지 않은 변수를 사용함

## [#1922](https://github.com/tc39/ecma262/pull/1922/files): Editorial: Treat not present flatMap parameter as undefined

- **Version**: [f95a4da5213e0f6bcf69400d6a0e004a29952c43](https://github.com/tc39/ecma262/commits/f95a4da5213e0f6bcf69400d6a0e004a29952c43)
  - **Type**: Builtin
  - **Algorithm**: `Array.prototype.flatMap`
  - **Description**: 

## [#1915](https://github.com/tc39/ecma262/pull/1915/files): Editorial: added ~async-iterate~ in assertion of ForIn/OfHeadEvaluation

- **Version**: [6826d313a905d05e02daec1f4d2f22b911c960b4](https://github.com/tc39/ecma262/commits/6826d313a905d05e02daec1f4d2f22b911c960b4)
  - **Type**: Unreachable
  - **Algorithm**: `ForIn/OfHeadEvaluation`
  - **Description**: `iterationKind` 가 `~iterate~` 인 assertion 다음 `If iterationKind == ~async-iterate~` 라는 branch 가 있어 true branch 가 unreachble 임.

## [#1893](https://github.com/tc39/ecma262/pull/1893/files): [editorial] Fix typo in NewPromiseReactionJob

- **Version**: [c59502090e2c250cd7e457b5506b92db6b21d153](https://github.com/tc39/ecma262/commits/c59502090e2c250cd7e457b5506b92db6b21d153)
  - **Type**: Reference Error
  - **Algorithm**: `NewPromiseReactionJob`
  - **Description**: `handler` 라는 정의되지 않는 변수를 사용함

## [#1877](https://github.com/tc39/ecma262/pull/1877/files): Editorial: supply args to IteratorBindingInitialization

- **Version**: [2431eb385e4315471abd33f710b4ed5644e1b002](https://github.com/tc39/ecma262/commits/2431eb385e4315471abd33f710b4ed5644e1b002)
  - **Type**: Arity Mismatch
  - **Algorithm**: `ArrowParameters.IteratorBindingInitialization`
  - **Description**: `IteratorBindingInitialization` 은 2개의 parameter 가 있는 함수인데, 0개의 argument 로 호출하고 있음.

## [#1871](https://github.com/tc39/ecma262/pull/1871/files): Normative: ToInteger: fix spec bug from #1827 that allows (-1,0) to produce -0

- **Version**: [823aad1e08b5680229d67283371912950d19e581](https://github.com/tc39/ecma262/commits/823aad1e08b5680229d67283371912950d19e581)
  - **Type**: Cannot Detect
  - **Algorithm**: `ToInteger`
  - **Description**: `ToInteger` 가 -0 을 반환하면 안된다는 정보가 있어야함.

## [#1864](https://github.com/tc39/ecma262/pull/1864/files): Editorial: add missing argument to two CreateImmutableBinding calls

- **Version**: [bf37eb35b715b14e7a8f8c73059e11da75f7944a](https://github.com/tc39/ecma262/commits/bf37eb35b715b14e7a8f8c73059e11da75f7944a)
  - **Type**: Arity Mismatch
  - **Algorithm**: `AsyncGeneratorExpression.Evaluation`
  - **Description**: `CreateImmutableBinding` 함수는 2개의 parameter 가 있는 함수인데, 0개의 argument 로 호출하고 있음.

## [#1826](https://github.com/tc39/ecma262/pull/1826/files): Normative: Add missing ReturnIfAbrupt to “Evaluation of in expression”

- **Version**: [a329eefaca95fb1f91cf3828249e54f13b27e095](https://github.com/tc39/ecma262/commits/a329eefaca95fb1f91cf3828249e54f13b27e095)
  - **Type**: Unchecked Abrupt Completion
  - **Algorithm**: `RelationalExpression.Evaluation`
  - **Description**: `ToPropertyKey` 호출 시 abrupt completion 이 반환될 수 있지만, 이를 검사하지 않고 그대로 HasProperty 함수의 argument 로 넘기고 있음.
    - 현재 분석기에서는 `typeof` 계산을 하기위해 pure value 를 얻는 과정에서 알람을 띄울 수 있을 것으로 생각됨.

## [#1781](https://github.com/tc39/ecma262/pull/1781/files): Editorial: A couple fixes from OrdinaryFunctionCreate and undefined arguments

- **Version**: [12a546b92275a0e2f834017db2727bb9c6f6c8fd](https://github.com/tc39/ecma262/commits/12a546b92275a0e2f834017db2727bb9c6f6c8fd)
  - **Type**: Builtin
  - **Algorithm**: `BigInt.prototype.toString`, `DataView.prorotype.getBigInt64`, `DataView.prototype.getBigUint64`
  - **Description**: #1922 와 동일한 이슈임.

## [#1775](https://github.com/tc39/ecma262/pull/1775/files): Normative: Make super() throw after evaluating args

- **Version**: [2669d458ec0d5c2ccce3b105b288ec57f81aee4f](https://github.com/tc39/ecma262/commits/2669d458ec0d5c2ccce3b105b288ec57f81aee4f)
  - **Type**: Cannot Detect
  - **Algorithm**: `SuperCall.Evaluation`
  - **Description**: Semantics 상의 버그임.

## [#1752](https://github.com/tc39/ecma262/pull/1752/files): Editorial: Refactor index checking for Integer-Indexed exotic objects

- **Version**: [7fc703fd7e4241c103d9c2187033a90a984905d4](https://github.com/tc39/ecma262/commits/7fc703fd7e4241c103d9c2187033a90a984905d4)
  - **Type**: Cannot Detect
  - **Algorithm**: `IntegerIndexedObject.HasProperty`
  - **Description**: #2098과 같은 이슈임.

## [#1745](https://github.com/tc39/ecma262/pull/1745/files): add missing ContainsUseStrict definition for AsyncConciseBody

- **Version**: [02b37cdbf2a599a37f77c82f38d5146836ec84e1](https://github.com/tc39/ecma262/commits/02b37cdbf2a599a37f77c82f38d5146836ec84e1)
  - **Type**: Missing Case
  - **Algorithm**: `AsyncConciseBody.ContainsUseStrict`
  - **Description**: `AsyncConciseBody`의 `ContainsUseStrict` 함수가 없었음.

## [#1722](https://github.com/tc39/ecma262/pull/1722/files): Editorial: quick fixes for recent merges

- **Version**: [693e09a4b9ce52b060ceda897b042c3f83f0a738](https://github.com/tc39/ecma262/commits/693e09a4b9ce52b060ceda897b042c3f83f0a738)
  - **Type**: Builtin / Reference Error
  - **Algorithm**: `EnterCriticalSection`
  - **Description**: `eventRecords` 라는 없는 변수를 사용함.

## [#1704](https://github.com/tc39/ecma262/pull/1704/files): Editorial: quick fixes for recently-merged commits

- **Version**: [dc00d4df17e860704783bed0b7f19b2a40b56d88](https://github.com/tc39/ecma262/commits/dc00d4df17e860704783bed0b7f19b2a40b56d88)
  - **Type**: Builtin / Reference Error 
  - **Algorithm**: `AtomicReadModifyWrite`
  - **Description**: `v` 라는 없는 변수를 사용함.

  - **Type**: Builtin / Reference Error 
  - **Algorithm**: `Atomics.store`, `Atomics.wait`, `Atomics.notify`
  - **Description**: `arrayTypeName` 이라는 없는 변수를 사용함.

  - **Type**: Duplicated Definition
  - **Algorithm**: `BigIntBitwiseOp`, `BigInt`, ...
  - **Description**: `set` 대신 `let` 을 사용하여 같은 변수가 2번 정의되는 문제가 있었음.
