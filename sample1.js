// Copyright (C) 2017 Leo Balter. All rights reserved.
// This code is governed by the BSD license found in the LICENSE file.

/*---
esid: sec-array-constructor
description: >
  The "name" property of Array
info: |
  17 ECMAScript Standard Built-in Objects

  Every built-in Function object, including constructors, that is not
  identified as an anonymous function has a name property whose value is a
  String. Unless otherwise specified, this value is the name that is given to
  the function in this specification.

  [...]

  Unless otherwise specified, the name property of a built-in Function
  object, if it exists, has the attributes { [[Writable]]: false,
  [[Enumerable]]: false, [[Configurable]]: true }.
includes: [propertyHelper.js]
---*/

print(1111)
if(Array.name) print("yeah")
assert.sameValue(Array.name, 'Array');
print(2222)
verifyNotEnumerable(Array, 'name');
print(3333)
verifyNotWritable(Array, 'name');
print(4444)
verifyConfigurable(Array, 'name');
print(5555)
