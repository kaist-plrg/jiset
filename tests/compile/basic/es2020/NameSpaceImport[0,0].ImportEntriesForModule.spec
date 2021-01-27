          1. Let _localName_ be the StringValue of |ImportedBinding|.
          1. Let _entry_ be the ImportEntry Record { [[ModuleRequest]]: _module_, [[ImportName]]: *"\*"*, [[LocalName]]: _localName_ }.
          1. Return a new List containing _entry_.