## [#](): 

- **Version**: []()
  - **Type**:
  - **Algorithm**:
  - **Description**:

# Target Errors

## [#2121](https://github.com/tc39/ecma262/pull/2121/files): Editorial: reference correct nonterminals in IteratorDestructuringAssignmentEvaluation

- **Version**: [cb3a24d910c8a7ac62ee7b2883e4b2ce54b18add](https://github.com/tc39/ecma262/pull/2121/commits/cb3a24d910c8a7ac62ee7b2883e4b2ce54b18add)
  - **Type**: Reference Error - `AssignmentExpression`, `LeftHandSideExpression`
  - **Algorithm**: AssignmentExpression.IteratorDestructuringAssignmentEvaluation
  - **Description**: `AssignmentExpression`과 `LeftHandSideExpression`이 없는데 사용함

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


