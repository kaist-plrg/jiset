        1. Assert: _nonterminal_ is a parsed grammar production.
        1. If the source code matching _nonterminal_ is not strict code, return *false*.
        1. If _nonterminal_ is not contained within a |FunctionBody| or |ConciseBody|, return *false*.
        1. Let _body_ be the |FunctionBody| or |ConciseBody| that most closely contains _nonterminal_.
        1. If _body_ is the |FunctionBody| of a |GeneratorBody|, return *false*.
        1. Return the result of HasProductionInTailPosition of _body_ with argument _nonterminal_.