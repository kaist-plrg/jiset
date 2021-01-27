            1. If Type(_O_) is not Object, return *false*.
            1. Let _spreadable_ be ? Get(_O_, @@isConcatSpreadable).
            1. If _spreadable_ is not *undefined*, return ToBoolean(_spreadable_).
            1. Return ? IsArray(_O_).