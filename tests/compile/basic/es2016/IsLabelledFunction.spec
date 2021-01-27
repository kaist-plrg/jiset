        1. If _stmt_ is not a |LabelledStatement|, return *false*.
        1. Let _item_ be the |LabelledItem| component of _stmt_.
        1. If _item_ is <emu-grammar>LabelledItem : FunctionDeclaration</emu-grammar> , return *true*.
        1. Let _subStmt_ be the |Statement| component of _item_.
        1. Return IsLabelledFunction(_subStmt_).