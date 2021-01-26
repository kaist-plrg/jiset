          1. Let _module_ be the sole element of ModuleRequests of |FromClause|.
          1. Let _entry_ be the ExportEntry Record {[[ModuleRequest]]: _module_, [[ImportName]]: `"*"`, [[LocalName]]: *null*, [[ExportName]]: *null* }.
          1. Return a new List containing _entry_.