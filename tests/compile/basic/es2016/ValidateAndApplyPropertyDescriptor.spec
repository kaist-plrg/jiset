          1. Assert: If _O_ is not *undefined*, then IsPropertyKey(_P_) is *true*.
          1. If _current_ is *undefined*, then
            1. If _extensible_ is *false*, return *false*.
            1. Assert: _extensible_ is *true*.
            1. If IsGenericDescriptor(_Desc_) is *true* or IsDataDescriptor(_Desc_) is *true*, then
              1. If _O_ is not *undefined*, create an own data property named _P_ of object _O_ whose [[Value]], [[Writable]], [[Enumerable]] and [[Configurable]] attribute values are described by _Desc_. If the value of an attribute field of _Desc_ is absent, the attribute of the newly created property is set to its default value.
            1. Else _Desc_ must be an accessor Property Descriptor,
              1. If _O_ is not *undefined*, create an own accessor property named _P_ of object _O_ whose [[Get]], [[Set]], [[Enumerable]] and [[Configurable]] attribute values are described by _Desc_. If the value of an attribute field of _Desc_ is absent, the attribute of the newly created property is set to its default value.
            1. Return *true*.
          1. Return *true*, if every field in _Desc_ is absent.
          1. Return *true*, if every field in _Desc_ also occurs in _current_ and the value of every field in _Desc_ is the same value as the corresponding field in _current_ when compared using the SameValue algorithm.
          1. If the [[Configurable]] field of _current_ is *false*, then
            1. Return *false*, if the [[Configurable]] field of _Desc_ is *true*.
            1. Return *false*, if the [[Enumerable]] field of _Desc_ is present and the [[Enumerable]] fields of _current_ and _Desc_ are the Boolean negation of each other.
          1. If IsGenericDescriptor(_Desc_) is *true*, no further validation is required.
          1. Else if IsDataDescriptor(_current_) and IsDataDescriptor(_Desc_) have different results, then
            1. Return *false*, if the [[Configurable]] field of _current_ is *false*.
            1. If IsDataDescriptor(_current_) is *true*, then
              1. If _O_ is not *undefined*, convert the property named _P_ of object _O_ from a data property to an accessor property. Preserve the existing values of the converted property's [[Configurable]] and [[Enumerable]] attributes and set the rest of the property's attributes to their default values.
            1. Else,
              1. If _O_ is not *undefined*, convert the property named _P_ of object _O_ from an accessor property to a data property. Preserve the existing values of the converted property's [[Configurable]] and [[Enumerable]] attributes and set the rest of the property's attributes to their default values.
          1. Else if IsDataDescriptor(_current_) and IsDataDescriptor(_Desc_) are both *true*, then
            1. If the [[Configurable]] field of _current_ is *false*, then
              1. Return *false*, if the [[Writable]] field of _current_ is *false* and the [[Writable]] field of _Desc_ is *true*.
              1. If the [[Writable]] field of _current_ is *false*, then
                1. Return *false*, if the [[Value]] field of _Desc_ is present and SameValue(_Desc_.[[Value]], _current_.[[Value]]) is *false*.
            1. Else the [[Configurable]] field of _current_ is *true*, so any change is acceptable.
          1. Else IsAccessorDescriptor(_current_) and IsAccessorDescriptor(_Desc_) are both *true*,
            1. If the [[Configurable]] field of _current_ is *false*, then
              1. Return *false*, if the [[Set]] field of _Desc_ is present and SameValue(_Desc_.[[Set]], _current_.[[Set]]) is *false*.
              1. Return *false*, if the [[Get]] field of _Desc_ is present and SameValue(_Desc_.[[Get]], _current_.[[Get]]) is *false*.
          1. If _O_ is not *undefined*, then
            1. For each field of _Desc_ that is present, set the corresponding attribute of the property named _P_ of object _O_ to the value of the field.
          1. Return *true*.