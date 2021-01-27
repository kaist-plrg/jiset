          1. If _Desc_ is *undefined*, return *undefined*.
          1. Let _obj_ be OrdinaryObjectCreate(%Object.prototype%).
          1. Assert: _obj_ is an extensible ordinary object with no own properties.
          1. If _Desc_ has a [[Value]] field, then
            1. Perform ! CreateDataPropertyOrThrow(_obj_, *"value"*, _Desc_.[[Value]]).
          1. If _Desc_ has a [[Writable]] field, then
            1. Perform ! CreateDataPropertyOrThrow(_obj_, *"writable"*, _Desc_.[[Writable]]).
          1. If _Desc_ has a [[Get]] field, then
            1. Perform ! CreateDataPropertyOrThrow(_obj_, *"get"*, _Desc_.[[Get]]).
          1. If _Desc_ has a [[Set]] field, then
            1. Perform ! CreateDataPropertyOrThrow(_obj_, *"set"*, _Desc_.[[Set]]).
          1. If _Desc_ has an [[Enumerable]] field, then
            1. Perform ! CreateDataPropertyOrThrow(_obj_, *"enumerable"*, _Desc_.[[Enumerable]]).
          1. If _Desc_ has a [[Configurable]] field, then
            1. Perform ! CreateDataPropertyOrThrow(_obj_, *"configurable"*, _Desc_.[[Configurable]]).
          1. Return _obj_.