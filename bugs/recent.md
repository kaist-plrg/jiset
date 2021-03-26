# ECMAScript 2021 Bugs

## IfStatement - [[Merged](https://github.com/tc39/ecma262/pull/2359)]
- __Section:__ [14.6.1 Static Semantics: Early Errors](https://tc39.es/ecma262/#sec-if-statement-static-semantics-early-errors)
- IfStatement[0,0].EarlyErrors에서 2개의 Statement parameter가 있기 때문에, 본문에서 Statement를 접근할 때 reference error가 발생
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

## ClassTail - [[Reported](https://github.com/tc39/ecma262/pull/2362)]
- __Section:__ [8.4.1 Static Semantics: Contains](https://tc39.es/ecma262/#sec-static-semantics-contains)
- ClassTail[0,3].Contains에서 3번째 statement에서 ClassHeritage에 대한 검사 없이 사용하여서 reference error가 발생
- __Current:__

```
3. Let inHeritage be ClassHeritage Contains symbol.
```

- __Expected:__

```
3. If ClassHeritage is present, let inHeritage be ClassHeritage Contains symbol. Otherwise, let inHeritage be false.
```
