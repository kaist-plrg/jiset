          1. Let _unscopableList_ be ObjectCreate(*null*).
          1. Perform CreateDataProperty(_unscopableList_, `"copyWithin"`, *true*).
          1. Perform CreateDataProperty(_unscopableList_, `"entries"`, *true*).
          1. Perform CreateDataProperty(_unscopableList_, `"fill"`, *true*).
          1. Perform CreateDataProperty(_unscopableList_, `"find"`, *true*).
          1. Perform CreateDataProperty(_unscopableList_, `"findIndex"`, *true*).
          1. Perform CreateDataProperty(_unscopableList_, `"includes"`, *true*).
          1. Perform CreateDataProperty(_unscopableList_, `"keys"`, *true*).
          1. Perform CreateDataProperty(_unscopableList_, `"values"`, *true*).
          1. Assert: Each of the above calls will return *true*.
          1. Return _unscopableList_.