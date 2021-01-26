          1. Assert: _Desc_ is a Property Descriptor.
          1. Let _like_ be Record{[[Value]]: *undefined*, [[Writable]]: *false*, [[Get]]: *undefined*, [[Set]]: *undefined*, [[Enumerable]]: *false*, [[Configurable]]: *false*}.
          1. If IsGenericDescriptor(_Desc_) is *true* or IsDataDescriptor(_Desc_) is *true*, then
            1. If _Desc_ does not have a [[Value]] field, set _Desc_.[[Value]] to _like_.[[Value]].
            1. If _Desc_ does not have a [[Writable]] field, set _Desc_.[[Writable]] to _like_.[[Writable]].
          1. Else,
            1. If _Desc_ does not have a [[Get]] field, set _Desc_.[[Get]] to _like_.[[Get]].
            1. If _Desc_ does not have a [[Set]] field, set _Desc_.[[Set]] to _like_.[[Set]].
          1. If _Desc_ does not have an [[Enumerable]] field, set _Desc_.[[Enumerable]] to _like_.[[Enumerable]].
          1. If _Desc_ does not have a [[Configurable]] field, set _Desc_.[[Configurable]] to _like_.[[Configurable]].
          1. Return _Desc_.