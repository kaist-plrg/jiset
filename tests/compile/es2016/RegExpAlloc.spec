            1. Let _obj_ be ? OrdinaryCreateFromConstructor(_newTarget_, `"%RegExpPrototype%"`, « [[RegExpMatcher]], [[OriginalSource]], [[OriginalFlags]] »).
            1. Perform ! DefinePropertyOrThrow(_obj_, `"lastIndex"`, PropertyDescriptor {[[Writable]]: *true*, [[Enumerable]]: *false*, [[Configurable]]: *false*}).
            1. Return _obj_.