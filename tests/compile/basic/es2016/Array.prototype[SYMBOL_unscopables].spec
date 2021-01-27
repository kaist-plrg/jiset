          1. Let _blackList_ be ObjectCreate(*null*).
          1. Perform CreateDataProperty(_blackList_, `"copyWithin"`, *true*).
          1. Perform CreateDataProperty(_blackList_, `"entries"`, *true*).
          1. Perform CreateDataProperty(_blackList_, `"fill"`, *true*).
          1. Perform CreateDataProperty(_blackList_, `"find"`, *true*).
          1. Perform CreateDataProperty(_blackList_, `"findIndex"`, *true*).
          1. Perform CreateDataProperty(_blackList_, `"includes"`, *true*).
          1. Perform CreateDataProperty(_blackList_, `"keys"`, *true*).
          1. Perform CreateDataProperty(_blackList_, `"values"`, *true*).
          1. Assert: Each of the above calls will return *true*.
          1. Return _blackList_.