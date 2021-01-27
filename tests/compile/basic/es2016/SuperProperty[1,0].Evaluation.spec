          1. Let _propertyKey_ be StringValue of |IdentifierName|.
          1. If the code matched by the syntactic production that is being evaluated is strict mode code, let _strict_ be *true*, else let _strict_ be *false*.
          1. Return ? MakeSuperPropertyReference(_propertyKey_, _strict_).