# ECMAScript 2021 Bugs

|ID|Feature|Bug Algo|Desc|Created|Status|
|:-:|:-:|:-:|:-:|:-:|:-:|
|ES12-1|CaseBlock|ContainsDuplicateLabels|DuplicatedVar(hasDuplicated)|ES2015|Merged|
|ES12-1|CaseBlock|ContainsUndefinedBreakTarget|DuplicatedVar(hasUndefinedLabels)|ES2015|Merged|
|ES12-1|CaseBlock|ContainsUndefinedContinueTarget|DuplicatedVar(hasUndefinedLabels)|ES2015|Merged|
|ES12-2|TryStatement|ContainsDuplicateLabels|DuplicatedVar(hasDuplicated)|ES2015|Merged|
|ES12-2|TryStatement|ContainsUndefinedBreakTarget|DuplicatedVar(hasUndefinedLabels)|ES2015|Merged|
|ES12-2|TryStatement|ContainsUndefinedContinueTarget|DuplicatedVar(hasUndefinedLabels)|ES2015|Merged|
|ES12-3|Arguments|CreateMappedArgumentsObject|DuplicatedVar(index)|ES2015|Merged|
|ES12-4|Array|\[\[DefineOwnProperty\]\]|DuplicatedVar(succeeded)|ES2015|Merged|
|ES12-4|Array|ArraySetLength|DuplicatedVar(succeded)|ES2015|Merged|
|ES12-5|YieldExpression|Evaluation|DuplicatedVar(value)|ES2015|Approved|
|ES12-6|ClassHeritage|Contains|UnknownVar(ClassHeritage)|ES2015|Merged|
|ES12-7|IfStatement|EarlyErrors|UnknownVar(Statement)|ES2015|Merged|
|ES12-8|Arguments|\[\[DefineOwnProperty\]\]|Unchecked Abrupt|2015.12.16|Approved|
|ES12-8|Arguments|\[\[GetOwnProperty\]\]|Unchecked Abrupt|2015.12.16|Approved|

## IfStatement - [[Merged](https://github.com/tc39/ecma262/pull/2359)]
- __Section:__ [14.6.1 Static Semantics: Early Errors](https://tc39.es/ecma262/#sec-if-statement-static-semantics-early-errors)
- IfStatement[0,0].EarlyErrors에서 2개의 Statement parameter가 있기 때문에, 본문에서 Statement를 접근할 때 reference error가 발생
- __Issue__: ES2015(commit: 7461eea82908b2c5742e4d6e5f57dc0cd4b43a60)
- __Current:__

```
if ( Expression ) Statement else Statement
if ( Expression ) Statement
- It is a Syntax Error if IsLabelledFunction(Statement) is true.
```

- __Expected:__

```
if ( Expression ) Statement else Statement
- It is a Syntax Error if IsLabelledFunction(the first Statement) is true.
- It is a Syntax Error if IsLabelledFunction(the second Statement) is true.
if ( Expression ) Statement
- It is a Syntax Error if IsLabelledFunction(Statement) is true.
```

## ClassTail - [[Merged](https://github.com/tc39/ecma262/pull/2362)]
- __Section:__ [8.4.1 Static Semantics: Contains](https://tc39.es/ecma262/#sec-static-semantics-contains)
- ClassTail[0,3].Contains에서 3번째 statement에서 ClassHeritage에 대한 검사 없이 사용하여서 reference error가 발생
- __Issue__: ES2015(commit: 7461eea82908b2c5742e4d6e5f57dc0cd4b43a60)
- __Current:__

```
3. Let inHeritage be ClassHeritage Contains symbol.
```

- __Expected:__

```
3. If ClassHeritage is present, let inHeritage be ClassHeritage Contains symbol. Otherwise, let inHeritage be false.
```

## Duplicated Variables - [[Merged](https://github.com/tc39/ecma262/pull/2365)]
- __Section:__ [8.2.1 Static Semantics: ContainsDuplicateLabels](https://tc39.es/ecma262/#sec-static-semantics-containsduplicatelabels)
- __Section:__ [8.2.2 Static Semantics: ContainsUndefinedBreakTarget](https://tc39.es/ecma262/#sec-static-semantics-containsundefinedbreaktarget)
- __Section:__ [8.2.3 Static Semantics: ContainsUndefinedContinueTarget](https://tc39.es/ecma262/#sec-static-semantics-containsundefinedcontinuetarget)
- __Issue:__ ES2015(commit: 7461eea82908b2c5742e4d6e5f57dc0cd4b43a60)
- __Count:__ 6
- __Current:__

```
CaseBlock : { CaseClauses? DefaultClause CaseClauses? }

1. If the first |CaseClauses| is present, then
  1. Let _hasDuplicates_ be ContainsDuplicateLabels of the first |CaseClauses| with argument _labelSet_.
  1. If _hasDuplicates_ is *true*, return *true*.
1. Let _hasDuplicates_ be ContainsDuplicateLabels of |DefaultClause| with argument _labelSet_.
1. If _hasDuplicates_ is *true*, return *true*.

TryStatement : try Block Catch Finally

1. Let _hasDuplicates_ be ContainsDuplicateLabels of |Block| with argument _labelSet_.
1. If _hasDuplicates_ is *true*, return *true*.
1. Let _hasDuplicates_ be ContainsDuplicateLabels of |Catch| with argument _labelSet_.
1. If _hasDuplicates_ is *true*, return *true*.
```

- __Expected:__

```
CaseBlock : { CaseClauses? DefaultClause CaseClauses? }

1. If the first |CaseClauses| is present, then
  1. If ContainsDuplicateLabels of the first |CaseClauses| with argument _labelSet_ is *true*, return *true*.
1. If ContainsDuplicateLabels of |DefaultClause| with argument _labelSet_ is *true*, return *true*.

TryStatement : try Block Catch Finally

1. If ContainsDuplicateLabels of |Block| with argument _labelSet_ is *true*, return *true*.
1. If ContainsDuplicateLabels of |Catch| with argument _labelSet_ is *true*, return *true*.
```

- __Section:__ [10.4.2.1 \[\[DefineOwnProperty\]\]](https://tc39.es/ecma262/#sec-array-exotic-objects-defineownproperty-p-desc)
- __Issue:__ ES2015(commit: 7461eea82908b2c5742e4d6e5f57dc0cd4b43a60)
- __Count:__ 1
- __Current:__

```
1. Let _succeeded_ be ! OrdinaryDefineOwnProperty(_A_, _P_, _Desc_).
1. If _succeeded_ is *false*, return *false*.
1. If _index_ &ge; _oldLen_, then
  1. Set _oldLenDesc_.[[Value]] to _index_ + *1*<sub>𝔽</sub>.
  1. Let _succeeded_ be OrdinaryDefineOwnProperty(_A_, *"length"*, _oldLenDesc_).
```

- __Expected:__

```
1. Let _succeeded_ be ! OrdinaryDefineOwnProperty(_A_, _P_, _Desc_).
1. If _succeeded_ is *false*, return *false*.
1. If _index_ &ge; _oldLen_, then
  1. Set _oldLenDesc_.[[Value]] to _index_ + *1*<sub>𝔽</sub>.
  1. Set _succeeded_ be OrdinaryDefineOwnProperty(_A_, *"length"*, _oldLenDesc_).
```

- __Section:__ [10.4.2.4 ArraySetLength](https://tc39.es/ecma262/#sec-arraysetlength)
- __Issue:__ ES2015(commit: 7461eea82908b2c5742e4d6e5f57dc0cd4b43a60)
- __Count:__ 1
- __Current:__

```
1. Let _succeeded_ be ! OrdinaryDefineOwnProperty(_A_, *"length"*, _newLenDesc_).
1. If _succeeded_ is *false*, return *false*.
...
1. If _newWritable_ is *false*, then
  1. Let _succeeded_ be ! OrdinaryDefineOwnProperty(_A_, *"length"*, PropertyDescriptor { [[Writable]]: *false* }).
```

- __Expected:__

```
1. Let _succeeded_ be ! OrdinaryDefineOwnProperty(_A_, *"length"*, _newLenDesc_).
1. If _succeeded_ is *false*, return *false*.
...
1. If _newWritable_ is *false*, then
  1. Set _succeeded_ be ! OrdinaryDefineOwnProperty(_A_, *"length"*, PropertyDescriptor { [[Writable]]: *false* }).
```

- __Section:__ [10.4.4.7 CreateMappedArgumentsObject](https://tc39.es/ecma262/#sec-createmappedargumentsobject)
- __Issue:__ ES2015(commit: 7461eea82908b2c5742e4d6e5f57dc0cd4b43a60)
- __Count:__ 1
- __Current:__

```
1. Let _index_ be 0.
...
1. Let _index_ be _numberOfParameters_ - 1.
```

- __Expected:__

```
1. Let _index_ be 0.
...
1. Set _index_ be _numberOfParameters_ - 1.
```

## Duplicated Variables - [[Approved](https://github.com/tc39/ecma262/pull/2372)]
- __Section:__ [15.5.5 Runtime Semantics: Evaluation](https://tc39.es/ecma262/#sec-generator-function-definitions-runtime-semantics-evaluation)
- __Issue:__ ES2015(commit: 7461eea82908b2c5742e4d6e5f57dc0cd4b43a60)
- __Count:__ 1
- __Current:__

```
1. Let _value_ be ? GetValue(_exprRef_).
...
      1. If _done_ is *true*, then
        1. Let _value_ be ? IteratorValue(_innerReturnResult_).
```

- __Expected:__

```
1. Let _value_ be ? GetValue(_exprRef_).
...
      1. If _done_ is *true*, then
        1. Set _value_ to ? IteratorValue(_innerReturnResult_).
```

## [[DefineOwnProperty]], [[GetOwnProperty]] - [[Approved](https://github.com/tc39/ecma262/pull/2372)]
- __Section:__ [10.4.4 Arguments Exotic Objects](https://tc39.es/ecma262/#sec-arguments-exotic-objects)
- __Issue:__ [2015.12.16](https://github.com/tc39/ecma262/pull/256)
- __Comment:__ 처음 `?`, `!` 이 도입된 commit 임
- __Count:__ 2
- __Current:__

```
1. Let _isMapped_ be ! HasOwnProperty(_map_, _P_).
1. If _isMapped_ is *true*, then
  1. Set _desc_.[[Value]] to Get(_map_, _P_).
```

- __Expected:__

```
1. Let _isMapped_ be ! HasOwnProperty(_map_, _P_).
1. If _isMapped_ is *true*, then
  1. Set _desc_.[[Value]] to ! Get(_map_, _P_).
```

## OrdinaryCreateFromConstructor - [[Reported](https://github.com/tc39/ecma262/pull/2379)]
- __Section:__ [10.1.13 OrdinaryCreateFromConstructor](https://tc39.es/ecma262/#sec-ordinarycreatefromconstructor)
- __Issue:__ ???
- __Count:__ 1
- __Current:__

```
OrdinaryCreateFromConstructor ( _constructor_, _intrinsicDefaultProto_ [ , _internalSlotsList_ ] )
  1. Let _proto_ be ? GetPrototypeFromConstructor(_constructor_, _intrinsicDefaultProto_).
  1. Return ! OrdinaryObjectCreate(_proto_, _internalSlotsList_).
```

- __Expected:__

```
OrdinaryCreateFromConstructor ( _constructor_, _intrinsicDefaultProto_ [ , _internalSlotsList_ ] )
  1. Let _proto_ be ? GetPrototypeFromConstructor(_constructor_, _intrinsicDefaultProto_).
  1. If _internalSlotsList_ is not present, set _internalSlotsList_ to a new empty List.
  1. Return ! OrdinaryObjectCreate(_proto_, _internalSlotsList_).
```
