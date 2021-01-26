          1. Let _sourceName_ be the StringValue of the first |IdentifierName|.
          1. Let _exportName_ be the StringValue of the second |IdentifierName|.
          1. If _module_ is *null*, then
            1. Let _localName_ be _sourceName_.
            1. Let _importName_ be *null*.
          1. Else,
            1. Let _localName_ be *null*.
            1. Let _importName_ be _sourceName_.
          1. Return a new List containing the ExportEntry Record {[[ModuleRequest]]: _module_, [[ImportName]]: _importName_, [[LocalName]]: _localName_, [[ExportName]]: _exportName_ }.