          1. Let _entries_ be a new empty List.
          1. Let _names_ be the BoundNames of |VariableStatement|.
          1. For each _name_ in _names_, do
            1. Append the ExportEntry Record { [[ModuleRequest]]: *null*, [[ImportName]]: *null*, [[LocalName]]: _name_, [[ExportName]]: _name_ } to _entries_.
          1. Return _entries_.