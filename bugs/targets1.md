# Target Errors

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
