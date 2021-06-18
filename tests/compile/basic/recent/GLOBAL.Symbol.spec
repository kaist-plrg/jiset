          1. If NewTarget is not *undefined*, throw a *TypeError* exception.
          1. If _description_ is *undefined*, let _descString_ be *undefined*.
          1. Else, let _descString_ be ? ToString(_description_).
          1. Return a new unique Symbol value whose [[Description]] value is _descString_.