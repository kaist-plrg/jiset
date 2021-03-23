# ECMAScript 2021 Bugs

## IfStatement - [Not Reported]
- __Section:__ [14.6.1 Static Semantics: Early Errors](https://tc39.es/ecma262/#sec-if-statement-static-semantics-early-errors)
- IfStatement[0,0].EarlyErrors 에서 2개의 Statement parameter 가 있기 때문에, 본문에서 Statement 를 접근할 때 reference error 가 발생
- __Current:__ 
```js
if ( Expression ) Statement else Statement
if ( Expression ) Statement
- It is a Syntax Error if IsLabelledFunction(Statement) is true.
```
- __Expected:__
```js
if ( Expression ) Statement else Statement
- It is a Syntax Error if IsLabelledFunction(the first Statement) is true.
- It is a Syntax Error if IsLabelledFunction(the second Statement) is true.

if ( Expression ) Statement
- It is a Syntax Error if IsLabelledFunction(Statement) is true.
```