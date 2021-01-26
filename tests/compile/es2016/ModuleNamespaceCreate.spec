          1. Assert: _module_ is a Module Record.
          1. Assert: _module_.[[Namespace]] is *undefined*.
          1. Assert: _exports_ is a List of String values.
          1. Let _M_ be a newly created object.
          1. Set _M_'s essential internal methods to the definitions specified in <emu-xref href="#sec-module-namespace-exotic-objects"></emu-xref>.
          1. Set _M_'s [[Module]] internal slot to _module_.
          1. Set _M_'s [[Exports]] internal slot to _exports_.
          1. Create own properties of _M_ corresponding to the definitions in <emu-xref href="#sec-module-namespace-objects"></emu-xref>.
          1. Set _module_.[[Namespace]] to _M_.
          1. Return _M_.