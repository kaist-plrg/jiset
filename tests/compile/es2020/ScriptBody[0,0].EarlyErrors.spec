* It is a Syntax Error if |StatementList| Contains `super` unless the source code containing `super` is eval code that is being processed by a direct eval. Additional early error rules for `super` within direct eval are defined in .
* It is a Syntax Error if |StatementList| Contains |NewTarget| unless the source code containing |NewTarget| is eval code that is being processed by a direct eval. Additional early error rules for |NewTarget| in direct eval are defined in .
* It is a Syntax Error if ContainsDuplicateLabels of |StatementList| with argument « » is *true*.
* It is a Syntax Error if ContainsUndefinedBreakTarget of |StatementList| with argument « » is *true*.
* It is a Syntax Error if ContainsUndefinedContinueTarget of |StatementList| with arguments « » and « » is *true*.