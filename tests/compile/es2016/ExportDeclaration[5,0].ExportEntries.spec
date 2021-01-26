          1. Let _names_ be BoundNames of |HoistableDeclaration|.
          1. Let _localName_ be the sole element of _names_.
          1. Return a new List containing the Record {[[ModuleRequest]]: *null*, [[ImportName]]: *null*, [[LocalName]]: _localName_, [[ExportName]]: `"default"`}.