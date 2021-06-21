          1. Let _localNames_ be a new empty List.
          1. For each ImportEntry Record _i_ of _importEntries_, do
            1. Append _i_.[[LocalName]] to _localNames_.
          1. Return _localNames_.