          1. If Type(_sym_) is not Symbol, throw a *TypeError* exception.
          1. For each element _e_ of the GlobalSymbolRegistry List (see <emu-xref href="#sec-symbol.for"></emu-xref>),
            1. If SameValue(_e_.[[Symbol]], _sym_) is *true*, return _e_.[[Key]].
          1. Assert: GlobalSymbolRegistry does not currently contain an entry for _sym_.
          1. Return *undefined*.