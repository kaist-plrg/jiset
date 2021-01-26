          1. If _symbol_ is a |ReservedWord|, return *false*.
          1. If _symbol_ is an |Identifier| and StringValue of _symbol_ is the same value as the StringValue of |IdentifierName|, return *true*.
          1. Return *false*.