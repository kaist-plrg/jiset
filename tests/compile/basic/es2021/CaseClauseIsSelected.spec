        1. Assert: _C_ is an instance of the production <emu-grammar>CaseClause : `case` Expression `:` StatementList?</emu-grammar>.
        1. Let _exprRef_ be the result of evaluating the |Expression| of _C_.
        1. Let _clauseSelector_ be ? GetValue(_exprRef_).
        1. Return the result of performing Strict Equality Comparison _input_ === _clauseSelector_.