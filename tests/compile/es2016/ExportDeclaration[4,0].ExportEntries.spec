          1. Let _entries_ be a new empty List.
          1. Let _names_ be the BoundNames of |Declaration|.
          1. Repeat for each _name_ in _names_,
            1. Append to _entries_ the Record {[[ModuleRequest]]: *null*, [[ImportName]]: *null*, [[LocalName]]: _name_, [[ExportName]]: _name_ }.
          1. Return _entries_.