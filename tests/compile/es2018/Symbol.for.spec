          1. Let _stringKey_ be ? ToString(_key_).
          1. For each element _e_ of the GlobalSymbolRegistry List, do
            1. If SameValue(_e_.[[Key]], _stringKey_) is *true*, return _e_.[[Symbol]].
          1. Assert: GlobalSymbolRegistry does not currently contain an entry for _stringKey_.
          1. Let _newSymbol_ be a new unique Symbol value whose [[Description]] value is _stringKey_.
          1. Append the Record { [[Key]]: _stringKey_, [[Symbol]]: _newSymbol_ } to the GlobalSymbolRegistry List.
          1. Return _newSymbol_.