# Target Errors

## [#2242](https://github.com/tc39/ecma262/pull/2242/files): Editorial: put underscores around alias name [Cannot Dectect]

- **Version**: [31f3c2b0c220cfea849a99c4f4ef22d93ddac14e](https://github.com/tc39/ecma262/commits/31f3c2b0c220cfea849a99c4f4ef22d93ddac14e)
  - **Type**: Cannot Detect
  - **Algorithm**: IntegerIndexedExoticObject.OwnPropertyKeys
  - **Description**: 둘 다 동일한 IR로 컴파일 됨
  - **Count**: 1

## [#2238](https://github.com/tc39/ecma262/pull/2238/files): Editorial: fix number types in Array.prototype.findIndex / RegExp.prototype[Symbol.search] / {Map,Set}.prototype.size [Cannot Detect / Builtin]

- **Version**: [3db2f0d7d48247c1cac23d21de1ef98ac352e09c](https://github.com/tc39/ecma262/commits/3db2f0d7d48247c1cac23d21de1ef98ac352e09c)
  - **Type**: Builtin / Cannot Detect
  - **Algorithm**: RegExp.prototype.@@search, Array.prototype.findIndex, Map.prototype.size, Set.prototype.size
  - **Description**: 둘 다 동일한 IR로 컴파일 됨 (mathematical value, numeric value 에 대한 자세한 분류가 필요함)
  - **Count**: 5

## [#2220](https://github.com/tc39/ecma262/pull/2220/files): Editorial: replace a few stray GetReferencedName invocations / re-define "super-reference" [Detected]

- **Version**: [a18acef2559a6f159047ca6fc0f3dd932231fefc](https://github.com/tc39/ecma262/commits/a18acef2559a6f159047ca6fc0f3dd932231fefc)
  - **Type**: Reference Error - `GetReferencedName`
  - **Algorithm**: AssignmentExpression.Evaluation
  - **Description**: `GetReferencedName` 이라는 존재하지 않는 함수를 사용함
  - **Count**: 3
  - **Alarms**:
  ```
  [Bug] unknown variable: GetReferencedName @ AssignmentExpression[6,0].Evaluation:Call[5406]:[☊(AssignmentExpression), ☊(LeftHandSideExpression), ☊(AssignmentExpression)]
  [Bug] unknown variable: GetReferencedName @ AssignmentExpression[7,0].Evaluation:Call[5432]:[☊(AssignmentExpression), ☊(LeftHandSideExpression), ☊(AssignmentExpression)]
  [Bug] unknown variable: GetReferencedName @ AssignmentExpression[8,0].Evaluation:Call[5456]:[☊(AssignmentExpression), ☊(LeftHandSideExpression), ☊(AssignmentExpression)]
  ```

## [#2185](https://github.com/tc39/ecma262/pull/2185/files): Editorial: handle NaN and infinities in Math.round [Cannot Detect / Builtin]

- **Version**: [11c45e805c4f95e1eb81deedbda05e1bcbc08db6](https://github.com/tc39/ecma262/commits/11c45e805c4f95e1eb81deedbda05e1bcbc08db6)
  - **Type**: Bulitin / Cannot Detect
  - **Algorithm**: Math.round
  - **Description**: 우선 `If _n_ is an integral Number` 라는 문장이 현재 컴파일이 안되고, 컴파일이 되어도 해결하기 어려운 문제로 보임.
    - +Infinity < 0.5 라는 것이 있을 때 타입 에러가 발생해야 된다고 생각했지만, 둘 다 AbsNum 으로 표현되어 에러가 발생하지 않음
    - Math.round 에 대한 정확한 spec 이 주어지지 않은 상황에서 문제가 있음을 알기는 힘들다고 생각이 됨.
  - **Count**: 1

## [#2174](https://github.com/tc39/ecma262/pull/2174/files): Editorial: quick fixes re Math functions [Cannot Detect / Builtin]

- **Version**: [575149cfd77aebcf3a129e165bd89e14caafc31c](https://github.com/tc39/ecma262/commits/575149cfd77aebcf3a129e165bd89e14caafc31c)
  - **Type**: Builtin / Cannot Detect / Possible
  - **Algorithm**: Math.round, Math.fround
  - **Description**: Need discussion
  - **Count**: 5
    - Math.fround: `ToNumber` 라인이 빠진 것을 1개로 셈
    - Math.round: 각각의 잘못된 변수 사용을 1개로 셈

## [#2121](https://github.com/tc39/ecma262/pull/2121/files): Editorial: reference correct nonterminals in IteratorDestructuringAssignmentEvaluation

- **Version**: [276af73369c33f132ec55197f82273d53eb9d89a](https://github.com/tc39/ecma262/commits/276af73369c33f132ec55197f82273d53eb9d89a)
  - **Type**: Reference Error - `AssignmentExpression`, `LeftHandSideExpression`
  - **Algorithm**: AssignmentElement.IteratorDestructuringAssignmentEvaluation
  - **Description**: `AssignmentExpression`과 `LeftHandSideExpression`이 없는데 사용함

## [#2098](https://github.com/tc39/ecma262/pull/2098/files): Editorial: add missing ! on calls to OrdinaryObjectCreate [Cannot Detect]

- **Version**: [4fa9dadbe47f5c76580bf2282b31333d0f36e3de](https://github.com/tc39/ecma262/commits/4fa9dadbe47f5c76580bf2282b31333d0f36e3de)
  - **Type**: Cannot Detect
  - **Algorithm**: Various
  - **Description**: `OrdinaryObjectCreate` 라는 함수가 애초에 abrupt completion 이 나오지 않는 상황이었다면, `!` 를 붙이는 것은 단순 assertion 을 추가하는 것이므로 버그라고 보기 힘듬.
    - 만약 `?` 를 붙여야 되는 상황이었다면, `!` 를 붙이면 안된다는 warning 을 띄울 수 있을 것 같음.

## [#1977](https://github.com/tc39/ecma262/pull/1977/files): Editorial: Use DefinePropertyOrThrow and ! prefix in GetTemplateObject [Cannot Detect]

- **Version**: [5370c6cc6e35c48f1d4e46e4aff4b76d6479323b](https://github.com/tc39/ecma262/commits/5370c6cc6e35c48f1d4e46e4aff4b76d6479323b)
  - **Type**: Cannot Detect
  - **Algorithm**: `GetTemplateObject`
  - **Description**: `GetTemplateObject` 는 abrupt completion 이 나오지 않는 함수이므로, `DefineOwnProperty` 함수를 호출할 때 assertion 을 추가해 주었음.
  
## [#1976](https://github.com/tc39/ecma262/pull/1976/files): Editorial: add prefix '?' on calling ToPrimitive in Abstract Equality Comparison

- **Version**: [0b988b7700de675331ac360d164c978d6ea452ec](https://github.com/tc39/ecma262/commits/0b988b7700de675331ac360d164c978d6ea452ec)
  - **Type**: Unchecked Abrupt Completion
  - **Algorithm**: `Abstract Equality Comparison`
  - **Description**: `ToPrimitive` 는 abrupt completion 을 반환할 수 있지만, 이를 확인하지 않고 `x` 라는 값과 `==` 연산을 하고 있음.
  - **Count**: 2
  - **Alarms**
  - **Status**
    - Old version of `ToPrimitive` has incomplete compilation, results in incomplete analysis. 
    - Following has compilation error, `??? "Else, in:{} out:{}"`
    ```
    ...
    1. If Type(_input_) is Object, then
       1. If _PreferredType_ is not present, let _hint_ be *"default"*.
       1. Else if _PreferredType_ is hint String, let _hint_ be *"string"*.
       1. Else,
          1. Assert: _PreferredType_ is hint Number.
          1. Let _hint_ be *"number"*.
    ...
    ``` 

## [#1969](https://github.com/tc39/ecma262/pull/1969/files): Editorial: Return an iterator *record* for for...in heads [Reference Error]

- **Version**: [0e83cd040fe1f374fb472f946587d942829e755a](https://github.com/tc39/ecma262/commits/0e83cd040fe1f374fb472f946587d942829e755a)
  - **Type**: Reference Error
  - **Algorithm**: `ForIn/OfHeadEvaluation`
  - **Description**: `ForIn/OfHeadEvaluation` 은 `Iterator Record` 를 반환해야 하지만, `Iterator Object` 를 반환하는 경우가 발생했음. 이는 결국 `ForIn/OfBodyEvaluation` 의 `iteratorRecord` 로 흘러가는데, 이후 `iteratorRecord` 의 field 에 접근할 때, reference error 가 나와야함
  - **Count**: 9
    - 13.7.5.11 Runtime Semantics: LabelledEvaluation 에서 `IterationStatement`의 9가지 production에 대해서 각각 다른 View를 가지고 `ForInOfBodyEvaluaiton`을 부를 때, `keyResult`라는 함수 argument에 `ForInOfHeadEvaluaiton`의 잘못된 결과가 전달됨. 
  - **Alarms**
  - **Status**
    - 언급한대로 `ForInOfBodyEvaluation`에서 `iteratorRecord`가 `Object`로 잘못 전달된채로 분석이 진행되는데,
      `app __x1__ = (Call iteratorRecord.NextMethod iteratorRecord.Iterator)` statement에서 property access를 할 때 alarm이 발생하지 않음.
      해당 statement에서 `iteratorRecord.NextMethod`를 transfer할 때 Reference Error가 발생해야 함.
    - 참고 : AbsState.scala:87 - `TODO if (check && t.isMustAbsent) alarm(s"unknown property: $base.$prop")`

## [#1954](https://github.com/tc39/ecma262/pull/1954/files): Editorial: Fixed typos in variable name oldvalue [Reference Error]

- **Version**: [731fc79549be6eb6eceec457822a124ed6c35da8](https://github.com/tc39/ecma262/commits/731fc79549be6eb6eceec457822a124ed6c35da8)
  - **Type**: Reference Error
  - **Algorithm**: `UpdateExpression.Evaluation`
  - **Description**: `oldvalue` 라는 정의되지 않은 변수를 사용함
  - **Count**: 4
  - **Alarms**
    ```    
[Bug] unknown variable: oldvalue @ UpdateExpression[3,0].Evaluation:Call[4776]:[☊(UpdateExpression), ☊(UnaryExpression)]
[Bug] unknown variable: oldvalue @ UpdateExpression[1,0].Evaluation:Call[4754]:[☊(UpdateExpression), ☊(LeftHandSideExpression)]
[Bug] unknown variable: oldvalue @ UpdateExpression[2,0].Evaluation:Call[4765]:[☊(UpdateExpression), ☊(LeftHandSideExpression)]
[Bug] unknown variable: oldvalue @ UpdateExpression[4,0].Evaluation:Call[4787]:[☊(UpdateExpression), ☊(UnaryExpression)]
    ```

## [#1922](https://github.com/tc39/ecma262/pull/1922/files): Editorial: Treat not present flatMap parameter as undefined [Cannot Detect / Builtin]

- **Version**: [f95a4da5213e0f6bcf69400d6a0e004a29952c43](https://github.com/tc39/ecma262/commits/f95a4da5213e0f6bcf69400d6a0e004a29952c43)
  - **Type**: Cannot Detect
  - **Algorithm**: `Array.prototype.flatMap`
  - **Description**: 

## [#1915](https://github.com/tc39/ecma262/pull/1915/files): Editorial: added ~async-iterate~ in assertion of ForIn/OfHeadEvaluation [Assertion Failed]

- **Version**: [6826d313a905d05e02daec1f4d2f22b911c960b4](https://github.com/tc39/ecma262/commits/6826d313a905d05e02daec1f4d2f22b911c960b4)
  - **Type**: Assertion Failed
  - **Algorithm**: `ForIn/OfHeadEvaluation`
  - **Description**: `iterationKind` 가 `~iterate~` 인 assertion 다음 `If iterationKind == ~async-iterate~` 라는 branch 가 있어 true branch 가 unreachble 임.
  - **Count**: 1
  - **Alarms**
    ```
[Bug] assertion failed: (= iterationKind CONST_iterate) @ ForInOfHeadEvaluation:Block[7454]:[[], ☊(AssignmentExpression), ~asyncDASHiterate~]
    ```

## [#1893](https://github.com/tc39/ecma262/pull/1893/files): [editorial] Fix typo in NewPromiseReactionJob [Reference Error]

- **Version**: [c59502090e2c250cd7e457b5506b92db6b21d153](https://github.com/tc39/ecma262/commits/c59502090e2c250cd7e457b5506b92db6b21d153)
  - **Type**: Reference Error
  - **Algorithm**: `NewPromiseReactionJob`
  - **Description**: `handler` 라는 정의되지 않는 변수를 사용함
  - **Count**: 1
  - **Status**
    - `NewPromiseReactionJob` 함수 내에서는 `job`이라는 클로저를 정의하는데, 그 안에 정의되는 `handler`는 클로저의 정의 밖에서 사용할 수 없기 때문에 뒤의 `1. Let _getHandlerRealmResult_ be GetFunctionRealm(_handler_).`에서 Reference Error가 발생해야 함.
    - 현재 해당 함수의 맨 처음 statement가 컴파일이 되지 않기 때문에 (정확히는 `job`이라는 클로저를 만드는 statement) 분석이 진행되지 않음.

## [#1877](https://github.com/tc39/ecma262/pull/1877/files): Editorial: supply args to IteratorBindingInitialization [Arity Mismatch]

- **Version**: [2431eb385e4315471abd33f710b4ed5644e1b002](https://github.com/tc39/ecma262/commits/2431eb385e4315471abd33f710b4ed5644e1b002)
  - **Type**: Arity Mismatch
  - **Algorithm**: `ArrowParameters.IteratorBindingInitialization`
  - **Description**: `IteratorBindingInitialization` 은 2개의 parameter 가 있는 함수인데, 0개의 argument 로 호출하고 있음.
  - **Count**: 1 
  - **Alarms**
  - **Status**
    - `ArrowParameters.IteratorBindingInitialization` 함수가 cover되지 않음.

## [#1871](https://github.com/tc39/ecma262/pull/1871/files): Normative: ToInteger: fix spec bug from #1827 that allows (-1,0) to produce -0 [Cannot Detect]

- **Version**: [823aad1e08b5680229d67283371912950d19e581](https://github.com/tc39/ecma262/commits/823aad1e08b5680229d67283371912950d19e581)
  - **Type**: Cannot Detect
  - **Algorithm**: `ToInteger`
  - **Description**: `ToInteger` 가 -0 을 반환하면 안된다는 정보가 있어야함.

## [#1864](https://github.com/tc39/ecma262/pull/1864/files): Editorial: add missing argument to two CreateImmutableBinding calls [Arity Mismatch]

- **Version**: [bf37eb35b715b14e7a8f8c73059e11da75f7944a](https://github.com/tc39/ecma262/commits/bf37eb35b715b14e7a8f8c73059e11da75f7944a)
  - **Type**: Arity Mismatch
  - **Algorithm**: `AsyncGeneratorExpression.Evaluation`
  - **Description**: `CreateImmutableBinding` 함수는 2개의 parameter 가 있는 함수인데, 0개의 argument 로 호출하고 있음.
  - **Count**: 1
  - **Alarm**
  - **Status**
    - `let envRec = funcEnv.EnvironmentRecord` statement에서 envRec 변수가 저장이 안되고 있다.
    - envRec이 없어 `CreateImmutableBinding`가 불리기 전에 Reference Error가 뜨고 해당하는 함수를 찾지 못한다.

## [#1826](https://github.com/tc39/ecma262/pull/1826/files): Normative: Add missing ReturnIfAbrupt to “Evaluation of in expression” [Unchecked Abrupt Completion]

- **Version**: [a329eefaca95fb1f91cf3828249e54f13b27e095](https://github.com/tc39/ecma262/commits/a329eefaca95fb1f91cf3828249e54f13b27e095)
  - **Type**: Unchecked Abrupt Completion
  - **Algorithm**: `RelationalExpression.Evaluation`
  - **Description**: `ToPropertyKey` 호출 시 abrupt completion 이 반환될 수 있지만, 이를 검사하지 않고 그대로 HasProperty 함수의 argument 로 넘기고 있음.
    - 현재 분석기에서는 `typeof` 계산을 하기위해 pure value 를 얻는 과정에서 알람을 띄울 수 있을 것으로 생각됨.
  - **Count**: 1
  - **Alarms**
  - **Status**
    - 현재 분석에서는 `RelationalExpression[6,0].Evaluation`의 함수 내에서 abrupt completion이 발생하지 않음. View 기준으로 exhaustive하게 cover했는지 확인 필요함.

## [#1781](https://github.com/tc39/ecma262/pull/1781/files): Editorial: A couple fixes from OrdinaryFunctionCreate and undefined arguments [Builtin]

- **Version**: [12a546b92275a0e2f834017db2727bb9c6f6c8fd](https://github.com/tc39/ecma262/commits/12a546b92275a0e2f834017db2727bb9c6f6c8fd)
  - **Type**: Builtin
  - **Algorithm**: `BigInt.prototype.toString`, `DataView.prorotype.getBigInt64`, `DataView.prototype.getBigUint64`
  - **Description**: #1922 와 동일한 이슈임.

## [#1775](https://github.com/tc39/ecma262/pull/1775/files): Normative: Make super() throw after evaluating args [Cannot Detect]

- **Version**: [2669d458ec0d5c2ccce3b105b288ec57f81aee4f](https://github.com/tc39/ecma262/commits/2669d458ec0d5c2ccce3b105b288ec57f81aee4f)
  - **Type**: Cannot Detect
  - **Algorithm**: `SuperCall.Evaluation`
  - **Description**: Semantics 상의 버그임.

## [#1752](https://github.com/tc39/ecma262/pull/1752/files): Editorial: Refactor index checking for Integer-Indexed exotic objects [Cannot Detect]

- **Version**: [7fc703fd7e4241c103d9c2187033a90a984905d4](https://github.com/tc39/ecma262/commits/7fc703fd7e4241c103d9c2187033a90a984905d4)
  - **Type**: Cannot Detect
  - **Algorithm**: `IntegerIndexedObject.HasProperty`
  - **Description**: #2098과 같은 이슈임.

## [#1745](https://github.com/tc39/ecma262/pull/1745/files): add missing ContainsUseStrict definition for AsyncConciseBody [Unknown Function]

- **Version**: [02b37cdbf2a599a37f77c82f38d5146836ec84e1](https://github.com/tc39/ecma262/commits/02b37cdbf2a599a37f77c82f38d5146836ec84e1)
  - **Type**: Missing Case
  - **Algorithm**: `AsyncConciseBody.ContainsUseStrict`
  - **Description**: `AsyncConciseBody`의 `ContainsUseStrict` 함수가 없었음.
  - **Count**: ?
  - **Alarms**
  - **Status**
    - 해당 함수를 call하는 경우가 cover되지 않음. (`AsyncArrowFunction[1,0].EarlyErrors`, `CreateDynamicFunction`에서 사용되나 분석 결과에 없음.) 

## [#1722](https://github.com/tc39/ecma262/pull/1722/files): Editorial: quick fixes for recent merges [Builtin / Reference Error]

- **Version**: [693e09a4b9ce52b060ceda897b042c3f83f0a738](https://github.com/tc39/ecma262/commits/693e09a4b9ce52b060ceda897b042c3f83f0a738)
  - **Type**: Builtin / Reference Error
  - **Algorithm**: `EnterCriticalSection`
  - **Description**: `eventRecords` 라는 없는 변수를 사용함.

## [#1704](https://github.com/tc39/ecma262/pull/1704/files): Editorial: quick fixes for recently-merged commits [Builtin / Reference Error, Duplicated Definition]

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

## [#1676](https://github.com/tc39/ecma262/pull/1676/files): Editorial: use ! CreateDataPropertyOrThrow instead of CreateDataProperty and an assert

- **Version**: [1d0fe7c85c8f81a4ea301498ac036a8ef37a2a3d](https://github.com/tc39/ecma262/commits/1d0fe7c85c8f81a4ea301498ac036a8ef37a2a3d)
  - **Type**: Possiblely detected completion warning
  - **Algorithm**: FromPropertyDescriptor, CreateArrayFromList, CopyDataProperties, CreateIterResultObject, CreateUnmappedArgumentsObject, CreateMappedArgumentsObject, AssignmentRestElement.IteratorDestructuringAssignmentEvaluation, BindingRestElement.IteratorBindingInitialization / builtin
  - **Description**: Changed the function from CreateDataProperty to CreateDataPropertyOrThrow, and added !

## [#1675](https://github.com/tc39/ecma262/pull/1675/files): Editorial: Add missing _direction_ parameter in extended regexp pattern evaluate semantics in annex b

- **Version**: [b3d48e36e772dc0b155be89b70d04169cefef92e](https://github.com/tc39/ecma262/commits/b3d48e36e772dc0b155be89b70d04169cefef92e)
  - **Type**:
  - **Algorithm**: builtin
  - **Description**:

## [#1636](https://github.com/tc39/ecma262/pull/1636/files): Editorial: misc fixes

- **Version**: [fcae34e3177d8e0cffe0d495bc75b3a7b9f94048](https://github.com/tc39/ecma262/commits/fcae34e3177d8e0cffe0d495bc75b3a7b9f94048)
  - **Type**: Possibly detected completion warning
  - **Algorithm**: ClassTail.ClassDefinitionEvaluation
  - **Description**: Added !

  - **Type**: Completion error
  - **Algorithm**: ExportDeclaration.Evaluation
  - **Description**: ? is missing

## [#1629](https://github.com/tc39/ecma262/pull/1629/files): Editorial: '_trimmedString_' -> '_S_' in parseInt

- **Version**: [5c9339cc51b0d8d9c428d48b9d3dc4798d265340](https://github.com/tc39/ecma262/commits/5c9339cc51b0d8d9c428d48b9d3dc4798d265340)
  - **Type**: Reference Error - Reference error
  - **Algorithm**: ParseInt
  - **Description**: _trimmedString_ is defined but it's name should be _S_

## [#1622](https://github.com/tc39/ecma262/pull/1622/files): Editorial: Use `!` on calls to `ToBoolean`

- **Version**: [4bac90f15853cc029abd8a418292c9bd73417cff](https://github.com/tc39/ecma262/commits/4bac90f15853cc029abd8a418292c9bd73417cff)
  - **Type**: Possiblely detected completion warning
  - **Algorithm**: ToPropertyDescriptor, IsRegExp, IteratorComplete, HasBinding, [[SetPrototypeOf]], [[IsExtensible]], [[PreventExtensions]], [[DefineOwnProperty]], [[HasProperty]], [[Set]], [[Delete]], UnaryExpression.Evaluation, InstanceofOperator, LogicalANDExpression.Evaluation, LogicalORExpression.Evaluation, ConditionalExpression.Evaluation, IfStatement.Evaluation, IterationStatement.LabelledEvaluation, ForBodyEvaluation / builtin
  - **Description**: Added ! in front of ToBoolean

## [#1608](https://github.com/tc39/ecma262/pull/1608/files): Normative: IsSimpleParameterList is false for standalone rest parameters

- **Version**: [c77f0081a197eeaaf5589bc7ebe306b1cc5c9162](https://github.com/tc39/ecma262/commits/c77f0081a197eeaaf5589bc7ebe306b1cc5c9162)
  - **Type**: Missing case
  - **Algorithm**: FormalParameters.IsSimpleParameterList
  - **Description**: FunctionRestParameter case is missing

## [#1607](https://github.com/tc39/ecma262/pull/1607/files): Add ! and ? before CreateBuiltinFunction and CreateArrayFromList

- **Version**: [31f3c2b0c220cfea849a99c4f4ef22d93ddac14e](https://github.com/tc39/ecma262/commits/31f3c2b0c220cfea849a99c4f4ef22d93ddac14e)
  - **Type**: Possibly detected completion error
  - **Algorithm**: Await, EnumerableOwnPropertyNames, CreateListIteratorRecord, CreateIntrinsics, MakeArgGetter, MakeArgSetter / builtin
  - **Description**: Added ! in front of CreateBuiltinFunction and CreateArrayFromList

## [#1596](https://github.com/tc39/ecma262/pull/1596): Editorial: misc

- **Version**: [dc1e21c454bd316810be1c0e7af0131a2d7f38e9](https://github.com/tc39/ecma262/commits/dc1e21c454bd316810be1c0e7af0131a2d7f38e9)
  - **Type**: Possibly detected completion warning
  - **Algorithm**: GetIterator
  - **Description**: change ? into ! in front of CreateAsyncFromSyncIterator

  - **Type**: Parsing fail
  - **Algorithm**: FunctionInitialize
  - **Description**: comma after else is missing

## [#1580](https://github.com/tc39/ecma262/pull/1580): Editorial: Move ToObject out of GetSubstitution

- **Version**: [143752135131e0318ea65e8ca70b82c98103890f](https://github.com/tc39/ecma262/commits/143752135131e0318ea65e8ca70b82c98103890f)
  - **Type**:
  - **Algorithm**: builtin
  - **Description**:

## [#1519](https://github.com/tc39/ecma262/pull/1519): Editorial: Remove unused steps from definitions of Contains

- **Version**: [32fe1a621c485080f6e1a1c0cc13a772b3f601bd](https://github.com/tc39/ecma262/commits/32fe1a621c485080f6e1a1c0cc13a772b3f601bd)
  - **Type**: Possibly detected unreachable step
  - **Algorithm**: LiteralPropertyName.Contains, MemberExpression.Contains, SuperProperty.Contains, CallExpression.Contains
  - **Description**: unused ifstmt

## [#1486](https://github.com/tc39/ecma262/pull/1486): Editorial: Don't read the [[Status]] of non-cyclic modules

- **Version**: [b13630057f1d43e39d7e64a0504fc31ab2dbdd2b](https://github.com/tc39/ecma262/commits/b13630057f1d43e39d7e64a0504fc31ab2dbdd2b)
  - **Type**: missing field
  - **Algorithm**: InnerModuleInstantiation, InnerModuleEvaluation, GetModuleNamespace
  - **Description**: [[Status]] is not field of abstract module record but cyclic module record

## [#1474](https://github.com/tc39/ecma262/pull/1474): Normative: Make Async-from-Sync iterator object inaccessible to ECMAScript code

- **Version**: [49b1071eef0085947e75eb22bc3f658082441b82](https://github.com/tc39/ecma262/commits/49b1071eef0085947e75eb22bc3f658082441b82)
  - **Type**:
  - **Algorithm**: builtin
  - **Description**:

## [#1470](https://github.com/tc39/ecma262/pull/1470): Editorial: Handle abrupt completion in AsyncFromSyncIteratorContinuation

- **Version**: [84d7b5aff49648be14b9097ab36163e457fa75b0](https://github.com/tc39/ecma262/commits/84d7b5aff49648be14b9097ab36163e457fa75b0)
  - **Type**:
  - **Algorithm**: builtin
  - **Description**:

## [#1431](https://github.com/tc39/ecma262/pull/1431): Editorial: Evaluate Disjunction :: Alternative with argument direction

- **Version**: [25e4b9bc8cb2776a9e3cc231eb61a4f15b68228f](https://github.com/tc39/ecma262/commits/25e4b9bc8cb2776a9e3cc231eb61a4f15b68228f)
  - **Type**:
  - **Algorithm**: builtin
  - **Description**:

## [#1403](https://github.com/tc39/ecma262/pull/1403): Editorial: Fix Completion handling for != and !==

- **Version**: [69d9e638031c6a16199cbd844b13ccd4b14826b5](https://github.com/tc39/ecma262/commits/69d9e638031c6a16199cbd844b13ccd4b14826b5)
  - **Type**: Completion error
  - **Algorithm**: EqualityExpression.Evaluation
  - **Description**: doesn't handle completion properly

## [#1402](https://github.com/tc39/ecma262/pull/1402): Editorial: Fix SubstitutionTemplate ArgumentListEvaluation

- **Version**: [ae77188c64085669f8c7cc859327f0817cdb260d](https://github.com/tc39/ecma262/commits/ae77188c64085669f8c7cc859327f0817cdb260d)
  - **Type**: Reference error
  - **Algorithm**: TemplateLiteral.ArgumentListEvaluation
  - **Description**: TemplateLiteral was undeclared

## [#1378](https://github.com/tc39/ecma262/pull/1378): Editorial: LeftHandSideExpression -> UnaryExpression in Static Semantics: Early Errors

- **Version**: [517f09a9d08e3c8a8963302c9d7c0d69b03e0004](https://github.com/tc39/ecma262/commits/517f09a9d08e3c8a8963302c9d7c0d69b03e0004)
  - **Type**: Reference error
  - **Algorithm**: EarlyErrors
  - **Description**: |LeftHandSideExpression| should be |UnaryExpression|

## [#1356](https://github.com/tc39/ecma262/pull/1356): Editorial: fix assertion in IteratorBindingInitialization

- **Version**: [a09fc232c137800dbf51b6204f37fdede4ba1646](https://github.com/tc39/ecma262/commits/a09fc232c137800dbf51b6204f37fdede4ba1646)
  - **Type**: Type error
  - **Algorithm**: FormalParameter.IteratorBindingInitialization, FunctionRestParameter.IteratorBindingInitialization
  - **Description**: it doesn't consider the case _environment_ is undefined

## [#1355](https://github.com/tc39/ecma262/pull/1355): Editorial: fix a misused completion

- **Version**: [c012f9c70847559a1d9dc0d35d35b27fec42911e](https://github.com/tc39/ecma262/commits/c012f9c70847559a1d9dc0d35d35b27fec42911e)
  - **Type**: Completion error
  - **Algorithm**: ModuleItemList.Evaluation
  - **Description**: _sl_.[[Value]] should be _sl_

## [#1330](https://github.com/tc39/ecma262/pull/1330): Editorial: add ‘!’ to CreateDataPropertyOrThrow calls in PropertyDefinitionEvaluation

- **Version**: [4306fd2d7f5853a9f75fd39bec4d96d8e686ba6e](https://github.com/tc39/ecma262/commits/4306fd2d7f5853a9f75fd39bec4d96d8e686ba6e)
  - **Type**: Possibly detected completion warning
  - **Algorithm**: PropertyDefinition.PropertyDefinitionEvaluation
  - **Description**: Added ! and some assert

## [#1310](https://github.com/tc39/ecma262/pull/): Editorial: fix some Iterator call completions

- **Version**: [691173a72434ccf0ac7cc0104d1eecb6d0c740e7](https://github.com/tc39/ecma262/commits/691173a72434ccf0ac7cc0104d1eecb6d0c740e7)
  - **Type**:
  - **Algorithm**: AsyncArrowBindingIdentifier.IteratorBindingInitialization / builtin
  - **Description**: Removed ? in front of IteratorStep

------

## [#266](https://github.com/tc39/ecma262/pull/266/files): Normative: change default function length value to not include optional arguments
- **Version**: [166ab2b1c279b107d30e9f078b70eca8bc6ab67a](https://github.com/tc39/ecma262/commit/166ab2b1c279b107d30e9f078b70eca8bc6ab67a)
  - **Type**: Cannot Detect (Style guide)
  - **Algorithm**: 
  - **Description**: Parameter list에서 공백을 넣어주거나, `<p>` 안에 설명글에서 변수를 지칭하는 reference에 변화가 있는 PR으로, 분석기로는 잡아낼 수 없는 스타일 가이드의 영역임.

## [#405](https://github.com/tc39/ecma262/pull/405/files): Normative: account for possible abrupt completion 

- **Version**: [ae8f4875a0f7d3adcd838fbee76b792fc58f5067](https://github.com/tc39/ecma262/commit/ae8f4875a0f7d3adcd838fbee76b792fc58f5067)
  - **Type**: Unhandled Abrupt Completion - `requestedModule.GetExportedNames`
  - **Algorithm**: GetExportedNames
  - **Description**: `requestedModules.GetExportedNames`에서 abrupt completion이리턴될 수 있음에도 불구하고 `ReturnIfAbrupt`를 통해 처리하지 않음.

## [#507](https://github.com/tc39/ecma262/pull/507/files): [editorial] Assert normal completion value 

- **Version**: [74df47434e37994dd001ba4c5f070691ad38a99a](https://github.com/tc39/ecma262/commit/74df47434e37994dd001ba4c5f070691ad38a99a)
  - **Type**: Possible - No abrupt completion
  - **Algorithm**: ModuleNamespaceExoticObject.Get 
  - **Description**: `_m_.ResolveExport` 함수를 콜하면 abrupt completion이 리턴되지 않는다는 semantic한 성질을 이용하여 `?`로 `ReturnIfAbrupt` 확인을 해주던 것을 `!`로 변경함.

## [#553](https://github.com/tc39/ecma262/pull/553/files): TypedArrays slice set operation does not throw

- **Version**: [13a46174de00a49e326d3b0dfa253dca4da3c5b1](https://github.com/tc39/ecma262/commit/13a46174de00a49e326d3b0dfa253dca4da3c5b1)
  - **Type**: Possible - No abrupt completion 
  - **Algorithm**: %TypedArray%.prototype.slice
  - **Description**: #507의 경우와 같이 `Set(A, !ToString...)` 함수에서 abrupt completion이 리턴되지 않는다는 것을 이용하여 !를 추가함.

## [#563](https://github.com/tc39/ecma262/pull/563/files): [editorial] Remove tautological condition 

- **Version**: [b76b237077dd71be6b695605d1d51af93a2e6ca4](https://github.com/tc39/ecma262/commit/b76b237077dd71be6b695605d1d51af93a2e6ca4)
  - **Type**: Cannot Detect (Unreachable Node)
  - **Algorithm**: Date.prototype.setYear
  - **Description**: `If _y_ is not *NaN* ...` 앞에 `If _y_ is *NaN* ..`이 있기 때문에, If문의 조건문이 항상 만족하지 않으므로 해당 If를 없앰. CFG 상에서 unreachable node를 찾아 그 statement 근처에서 warning을 줄 수 있을 것으로 보이나, 분석 타깃은 아님.

## [#579](https://github.com/tc39/ecma262/pull/579/files): [editorial] Assert normal completion value

- **Version**: [c44ceb0f607147be2454c45e09f5d5377406fd09](https://github.com/tc39/ecma262/commit/c44ceb0f607147be2454c45e09f5d5377406fd09)
  - **Type**: Possible - No abrupt completion
  - **Algorithm**: GetValue, PutValue
  - **Description**: assert문으로 인해 `undefined`, `null`이 나오지 않는다는 semantic한 성질에서 abrupt completion이 나오지 않는다는 사실을 알아낼 수 있음.

## [#609](https://github.com/tc39/ecma262/pull/609/files): Normative: Resolve template argument references

- **Version**: [023edfe607856a46e91afc56d6cd02b5014462ab](https://github.com/tc39/ecma262/commit/023edfe607856a46e91afc56d6cd02b5014462ab)
  - **Type**: Type Mismatch - `firstSub`, `sub`, `next`
  - **Algorithm**: TemplateLiteral[0,0].ArgumentListEvaluation, TemplateMiddleList[0,0].SubstitutionEvaluation, TemplateMiddleList[1,0].SubstitutionEvaluation
  - **Description**: 기존 함수의 `firstSub`, `sub`, `next` 변수의 경우 ReferenceRecord를 담게되고, 이를 Value로 바꿔주기 위해 GetValue 함수를 적용해야 하는 경우로 리스트 안의 값의 타입이 맞지 않는 에러가 발생해야함.

## [#700](https://github.com/tc39/ecma262/pull/700/files): AssignmentElement -> AssignmentRestElement

- **Version**: [b9c327f615618da5a9f030c008b0339b507e3289](https://github.com/tc39/ecma262/commit/b9c327f615618da5a9f030c008b0339b507e3289)
  - **Type**: Refernce Error - `AssignmentElement`
  - **Algorithm**: AssignmentRestElement[0,0].IteratorDestructuringAssignmentEvaluation
  - **Description**: 함수 인자로 주어진 `AssignmentRestElement`를 참조해야하나 실수로 `AssignmentElement`를 참조하는 문제로, Reference Error가 발생해야함.

## [#759](https://github.com/tc39/ecma262/pull/759/files): Editorial: annotate ToObject calls with

- **Version**: [f9d21ef7869d9e6f7abf11fbc71de39291119467](https://github.com/tc39/ecma262/commit/f9d21ef7869d9e6f7abf11fbc71de39291119467)
  - **Type**: Possible - No abrupt completion
  - **Algorithm**: ForInOfHeadEvaluaiton, Object, Object.assign, Object.prototype.toString 
  - **Description**: `ToObject` 함수 콜에 대해 `!`를 추가하는 PR으로, 이전 statement에서 argument가 `undefined`나 `null`이 아니라는 점을 이용하여 abrupt completion이 발생하지 않는 다는 점을 알 수 있음.   

## [#794](https://github.com/tc39/ecma262/pull/794/files): Misc editorial 

- **Version**: [d88d05fdfc44e71f0d1f37228dc53a6b54391e1b](https://github.com/tc39/ecma262/commit/d88d05fdfc44e71f0d1f37228dc53a6b54391e1b)
  - **Type**: No Return
  - **Algorithm**: HasPrimitiveBase, IsSuperReference
  - **Description**: 두 함수의 `Return *true* if (...)` statement에 else 브랜치가 존재하지 않아, else 브랜치의 노드에서 Return point가 없다는 에러가 발생함.

## [#834](https://github.com/tc39/ecma262/pull/834/files): Add missing local variable for [[ArrayBufferData]] in Atomics.wait/wake

- **Version**: [f26262cbbc4e9d7d72e7f50395d41c58255b30ea](https://github.com/tc39/ecma262/commit/f26262cbbc4e9d7d72e7f50395d41c58255b30ea)
  - **Type**: Reference Error - `block`
  - **Algorithm**: Atmoics.wait, Atomics.wake
  - **Description**: `_block_`이라는 변수가 존재하지 않음에도 이후 `GetWaiterList` 함수 콜에서 인자로 사용하고 있어, Reference Error가 발생해야함.

## [#870](https://github.com/tc39/ecma262/pull/870/files): Editorial: Delete spurious '?' before _O_.[[ArrayLength]] 

- **Version**: [0eba4b5c6aac4822d1082aab0f5f546b8dd46e6f](https://github.com/tc39/ecma262/commit/0eba4b5c6aac4822d1082aab0f5f546b8dd46e6f)
  - **Type**: Possible - No abrupt completion
  - **Algorithm**: %TypedArray%.ptototype.fill
  - **Description**: 앞선 `ValidateTypedArray` 함수 콜을 통해 `_O_` 가 valid하다는 것을 확신할 수 있고 (Conversation을 확인해본 결과 detached buffer에 잘못 접근하는 것을 막기 위한 validation) 따라서 `_O_.[[ArrayLength]]` access가 abrupt completion을 리턴하지 않음.  

## [#904](https://github.com/tc39/ecma262/pull/904/files): Editorial: Remove stray ! from async function spec

- **Version**: [7d142a9ce9319ddccb4613f5ab0c158243c136d7](https://github.com/tc39/ecma262/commit/7d142a9ce9319ddccb4613f5ab0c158243c136d7)
  - **Type**: Possible - No abrupt completion
  - **Algorithm**: AsyncFunctionBody[0,0].EvaluateBody, AsyncConciseBody[0,0].EvaluateBody
  - **Description**: `FunctionDeclarationInstantiation` 함수는 abrupt completion을 리턴하지 않기 때문에 (함수 내에 `ReturnIfAbrupt`가 사용되지 않고, `NormalCompletion(empty)` 만을 리턴함) 해당 함수 콜 앞에 `!`를 없애도 안전함.

## [#921](https://github.com/tc39/ecma262/pull/921/files): Editorial: Assert ToNumber does not throw in a case

- **Version**: [ea5f8c9583a840867481fd5d805210359ed75205](https://github.com/tc39/ecma262/commit/ea5f8c9583a840867481fd5d805210359ed75205)
  - **Type**: Possible - Assert no abrupt completion
  - **Algorithm**: AbstractEqualityComparison
  - **Description**: `ToNumber` 함수 자체는 인자 타입에 따라 abrupt completion을 리턴할 수 있지만, 타입 비교를 먼저 한 후 인자로 넘겨주기 때문에 abrupt completion이 발생하지 않는 것을 알 수 있음. 다만 `!`가 없었으나 추가해준 이 PR의 경우 스타일 가이드 정도 선에 그침.
  
## [#958](https://github.com/tc39/ecma262/pull/958/files): Editorial: Define "Let"/"Set"; correct usage

- **Version**: [785c05361f1ef8b4e8c8763ed6b118518dc80c10](https://github.com/tc39/ecma262/commit/785c05361f1ef8b4e8c8763ed6b118518dc80c10)
  - **Type**: Duplicate Variable Declaration - `accumulator`
  - **Algorithm**: Array.prototype.reduce, Array.prototype.reduceRight
  - **Description**: 두 함수 모두 앞선 if-else statement에서 Let statement로 변수 `accumulator`를 선언했음에도, 그 후 Repeat statement 안에서 다시 Let statement로 선언함. 

## [#1023](https://github.com/tc39/ecma262/pull/1023/files): Misc Editorial re recent commits

- **Version**: [d898df271008762e73e9408a1da2887835709e86](https://github.com/tc39/ecma262/commit/d898df271008762e73e9408a1da2887835709e86)
  - **Type**: Unknown Function - `CreateListIterator`
  - **Algorithm**:  FunctionDeclarationInstantiation
  - **Description**: `CreateListIterator`라는 존재하지 않는 함수를 콜함.

  - **Type**: Reference Error - `iteratorResult`
  - **Algorithm**: YieldExpression[2,0].Evaluation
  - **Description**: 이전에 선언된 변수인 `iteratorRecord`가 아닌 `iteratorResult`라는 변수를 참조하고 있음.

## [#1100](https://github.com/tc39/ecma262/pull/1100/files): misc editorial 

- **Version**: [17ebeea7386e2411e56f58f20c8d442ce91f5f42](https://github.com/tc39/ecma262/commit/17ebeea7386e2411e56f58f20c8d442ce91f5f42)
  - **Type**: Refernce Error - `excludedItems`
  - **Algorithm**: CopyDataProperties
  - **Description**: 함수 내에서 사용되는 `excludedItems`라는 인자가 존재하지 않았고 대신 함수 내에서 선언되는 `excluded`라는 변수 이름이 함수 인자에 존재하여, 이후 `excludedItems`를 참조할 때 Reference Error가 발생해야함.

## [#1127](https://github.com/tc39/ecma262/pull/1127/files): Normative: Strengthen Atomics.wait/notify synchronization to the level of other Atomics operations

- **Version**: [62225e4dd20b3221c9e7b9bd02374064d31e6b34](https://github.com/tc39/ecma262/commit/62225e4dd20b3221c9e7b9bd02374064d31e6b34)
  - **Type**: Cannot Detect - Semantic change
  - **Algorithm**: -
  - **Description**: 이 PR은 Candidate Execution 레코드의 `EventsList` 필드를 `EventRecords`로 바꾸는 것을 포함하여 Atomic synchronization과 관련된 semantic을 바꾸는 것으로, 정적 분석기가 검출하고자 하는 타깃 Error와 관련되지 않음.

## [#1182](https://github.com/tc39/ecma262/pull/1182/files): Misc Editorial 

- **Version**: [cff300b04f8e302a34e2cd28ebb9730a7f15d872](https://github.com/tc39/ecma262/commit/cff300b04f8e302a34e2cd28ebb9730a7f15d872)
  - **Type**: Possible - Assert no abrupt completion
  - **Algorithm**: SetFunctionLength
  - **Description**: #921과 같이 `ToInteger` 함수 콜에 `!`를 추가해주는 PR으로, 해당 함수 자체는 Abrupt completion을 리턴 가능하지만 함수 콜은 semantic하게 리턴하지 않는 것을 표시함.

  - **Type**: Possible - Duplicate Variable Declaration - `key`
  - **Algorithm**: DetachArrayBuffer
  - **Description**: `key`가 optional parameter로 이미 존재하나 Let statement를 통해 중복해서 선언하고 있음. (`If _key_ is not provided, let _key_ to be *undefined*`를 보았을 때, optional parameter가 인자로 전달되지 않은 경우에 대해 실수한 것으로 보임.)

## [#1231](https://github.com/tc39/ecma262/pull/1231/files): Editorial: remove unused variables in ArraySetLength

- **Version**: [b9822df4448e578184d5e8dc476c6df448de60a1](https://github.com/tc39/ecma262/commit/b9822df4448e578184d5e8dc476c6df448de60a1)
  - **Type**: Possible - Unused Variable - `succeeded`
  - **Algorithm**: ArraySetLength
  - **Description**: 리턴 직전에 `succeeded`라는 변수에 값을 저장하는데, 이후에는 사용되지 않는 변수임. 

## [#1263](https://github.com/tc39/ecma262/pull/1263/files): Editorial: fix variable name in Promise.race

- **Version**: [60beb210d8fc5dcbc3c4eb4f0aef718531ac603c](https://github.com/tc39/ecma262/commit/60beb210d8fc5dcbc3c4eb4f0aef718531ac603c)
  - **Type**: Reference Error - `iterator`
  - **Algorithm**: Promise.race
  - **Description**: 함수 내에서 이전에 선언되지 않은 변수 `iterator`를 참조하고 있음.

## [#1286](https://github.com/tc39/ecma262/pull/1286/files): Normative: Use GetValue to evaluate class heritage. 

- **Version**: [d560d3be3128d7d22bb57a5859ada15d95e2acc7](https://github.com/tc39/ecma262/commit/d560d3be3128d7d22bb57a5859ada15d95e2acc7)
  - **Type**: Type Mismatch - `superclass`
  - **Algorithm**: ClassTail[0,3].ClassDefinitionEvaluation
  - **Description**: #609와 같은 경우로, `|ClassHeritage|`를 eval한 후 Reference 타입의 결과값을 `GetValue` 함수를 적용하지 않고 사용했기 때문에 type mismatch 가 발견되어야함. 

## [#1298](https://github.com/tc39/ecma262/pull/1298/files): Normative: Add missing GetValue() to TemplateLiteral evaluation

- **Version**: [f3e73e7225827cade38adb81c85b8c1795874a25](https://github.com/tc39/ecma262/commit/f3e73e7225827cade38adb81c85b8c1795874a25)
  - **Type**: Type Mismatch - `sub`
  - **Algorithm**: SubstitutionTemplate[0,0].Evaluation, TemplateMiddleList[0,0].Evaluation, TemplateMiddleList[1,0].Evaluation 
  - **Description**: #609와 같은 경우로, `|Expression|을 eval한 후 Refernce 타입의 결과값을 `GetValue`함수를 적용하지 않고 사용했ㅈ기 때문에 type mismatch가 발견되어야함.

## [#1300](https://github.com/tc39/ecma262/pull/1300/files): Misc Editorial

- **Version**: [46eb1380fb47d84bc904a40904609042328c5eeb](https://github.com/tc39/ecma262/commit/46eb1380fb47d84bc904a40904609042328c5eeb)
  - **Type**: Duplicate Variable Declaration - `v`
  - **Algorithm**: AssignmentProperty[0,1].PropertyDestructuringAssignmentEvaluation, SingleNameBinding[0,1].IteratorBindingInitialization, SingleNameBinding[0,1].KeyedBindingInitialization
  - **Description**: 함수 내에서 변수 `v`가 이미 선언되어있음에도 Let statement를 통해 중복해서 선언하고 있음.

  - **Type**: Unhandled Abrupt Completion
  - **Algorithm**: ClassDeclaration[1,0].BindingClassDeclarationEvaluation
  - **Description**: 함수 내에서 `ClassDefinitionEvaluation`이라는 abrupt completion이 리턴될 수 있는 함수를 콜하기 때문에 이에 대한 `ReturnIfAbrupt` 처리가 필요함.

  - **Type**: No Return
  - **Algorithm**: CreateDataPropertyOnObjectFunction
  - **Description**: Return statement가 존재하지 않는 함수임.
  
  - **Type**: Type Mismatch - `func`
  - **Algorithm**: Function.prototype.toString
  - **Description**: `func` 변수의 프로퍼티에 접근하고 있기 때문에, `func` 변수가 먼저 오브젝트임을 확인해주어야 함.

  - **Type**: Reference Error - `notifierEvent`
  - **Algorithm**: NotifyWaiter
  - **Description**: 함수 내에 선언된 적 없는 변수 `notifierEvent`를 참조함. (`notifyEvent`의 오타)

## [#1301](https://github.com/tc39/ecma262/pull/1301/files): Normative: plug some holes in the coverage of syntax-directed operations 

- **Version**: [386b9b5f29bacc871d5e4f4b081eb24ea99fe5a5](https://github.com/tc39/ecma262/commit/386b9b5f29bacc871d5e4f4b081eb24ea99fe5a5)
  - **Type**: Missing Case
  - **Algorithm**: ... (see the commit log of #1301)
  - **Description**: 많은 syntax production의 경우에 대해서 Syntax directed operation이 정의되지 않았기 때문에, 이들을 추가해주는 PR임.


