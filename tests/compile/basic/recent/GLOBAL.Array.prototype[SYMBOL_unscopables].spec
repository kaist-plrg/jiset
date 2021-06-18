          1. Let _unscopableList_ be ! OrdinaryObjectCreate(*null*).
          1. Perform ! CreateDataPropertyOrThrow(_unscopableList_, *"copyWithin"*, *true*).
          1. Perform ! CreateDataPropertyOrThrow(_unscopableList_, *"entries"*, *true*).
          1. Perform ! CreateDataPropertyOrThrow(_unscopableList_, *"fill"*, *true*).
          1. Perform ! CreateDataPropertyOrThrow(_unscopableList_, *"find"*, *true*).
          1. Perform ! CreateDataPropertyOrThrow(_unscopableList_, *"findIndex"*, *true*).
          1. Perform ! CreateDataPropertyOrThrow(_unscopableList_, *"flat"*, *true*).
          1. Perform ! CreateDataPropertyOrThrow(_unscopableList_, *"flatMap"*, *true*).
          1. Perform ! CreateDataPropertyOrThrow(_unscopableList_, *"includes"*, *true*).
          1. Perform ! CreateDataPropertyOrThrow(_unscopableList_, *"keys"*, *true*).
          1. Perform ! CreateDataPropertyOrThrow(_unscopableList_, *"values"*, *true*).
          1. Return _unscopableList_.