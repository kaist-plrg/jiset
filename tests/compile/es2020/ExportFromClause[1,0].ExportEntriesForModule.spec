          1. Let _exportName_ be the StringValue of |IdentifierName|.
          1. Let _entry_ be the ExportEntry Record { [[ModuleRequest]]: _module_, [[ImportName]]: *"\*"*, [[LocalName]]: *null*, [[ExportName]]: _exportName_ }.
          1. Return a new List containing _entry_.