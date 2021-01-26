          1. Let _importName_ be the StringValue of |IdentifierName|.
          1. Let _localName_ be the StringValue of |ImportedBinding|.
          1. Let _entry_ be the ImportEntry Record { [[ModuleRequest]]: _module_, [[ImportName]]: _importName_, [[LocalName]]: _localName_ }.
          1. Return a new List containing _entry_.