          1. Assert: If _O_ is not *undefined*, then IsPropertyKey(_P_) is *true*.
          1. If _current_ is *undefined*, then
            1. If _extensible_ is *false*, return *false*.
            1. Assert: _extensible_ is *true*.
            1. If IsGenericDescriptor(_Desc_) is *true* or IsDataDescriptor(_Desc_) is *true*, then
              1. If _O_ is not *undefined*, create an own data property named _P_ of object _O_ whose [[Value]], [[Writable]], [[Enumerable]], and [[Configurable]] attribute values are described by _Desc_. If the value of an attribute field of _Desc_ is absent, the attribute of the newly created property is set to its <emu-xref href="#table-default-attribute-values">default value</emu-xref>.
            1. Else,
              1. Assert: ! IsAccessorDescriptor(_Desc_) is *true*.
              1. If _O_ is not *undefined*, create an own accessor property named _P_ of object _O_ whose [[Get]], [[Set]], [[Enumerable]], and [[Configurable]] attribute values are described by _Desc_. If the value of an attribute field of _Desc_ is absent, the attribute of the newly created property is set to its <emu-xref href="#table-default-attribute-values">default value</emu-xref>.
            1. Return *true*.
          1. If every field in _Desc_ is absent, return *true*.
          1. If _current_.[[Configurable]] is *false*, then
            1. If _Desc_.[[Configurable]] is present and its value is *true*, return *false*.
            1. If _Desc_.[[Enumerable]] is present and ! SameValue(_Desc_.[[Enumerable]], _current_.[[Enumerable]]) is *false*, return *false*.
          1. If ! IsGenericDescriptor(_Desc_) is *true*, then
            1. NOTE: No further validation is required.
          1. Else if ! SameValue(! IsDataDescriptor(_current_), ! IsDataDescriptor(_Desc_)) is *false*, then
            1. If _current_.[[Configurable]] is *false*, return *false*.
            1. If IsDataDescriptor(_current_) is *true*, then
              1. If _O_ is not *undefined*, convert the property named _P_ of object _O_ from a data property to an accessor property. Preserve the existing values of the converted property's [[Configurable]] and [[Enumerable]] attributes and set the rest of the property's attributes to their <emu-xref href="#table-default-attribute-values">default values</emu-xref>.
            1. Else,
              1. If _O_ is not *undefined*, convert the property named _P_ of object _O_ from an accessor property to a data property. Preserve the existing values of the converted property's [[Configurable]] and [[Enumerable]] attributes and set the rest of the property's attributes to their <emu-xref href="#table-default-attribute-values">default values</emu-xref>.
          1. Else if IsDataDescriptor(_current_) and IsDataDescriptor(_Desc_) are both *true*, then
            1. If _current_.[[Configurable]] is *false* and _current_.[[Writable]] is *false*, then
              1. If _Desc_.[[Writable]] is present and _Desc_.[[Writable]] is *true*, return *false*.
              1. If _Desc_.[[Value]] is present and SameValue(_Desc_.[[Value]], _current_.[[Value]]) is *false*, return *false*.
              1. Return *true*.
          1. Else,
            1. Assert: ! IsAccessorDescriptor(_current_) and ! IsAccessorDescriptor(_Desc_) are both *true*.
            1. If _current_.[[Configurable]] is *false*, then
              1. If _Desc_.[[Set]] is present and SameValue(_Desc_.[[Set]], _current_.[[Set]]) is *false*, return *false*.
              1. If _Desc_.[[Get]] is present and SameValue(_Desc_.[[Get]], _current_.[[Get]]) is *false*, return *false*.
              1. Return *true*.
          1. If _O_ is not *undefined*, then
            1. For each field of _Desc_ that is present, set the corresponding attribute of the property named _P_ of object _O_ to the value of the field.
          1. Return *true*.