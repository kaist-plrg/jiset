          1. If IsConstructor(_C_) is *false*, throw a *TypeError* exception.
          1. NOTE: _C_ is assumed to be a constructor function that supports the parameter conventions of the `Promise` constructor (see <emu-xref href="#sec-promise-executor"></emu-xref>).
          1. Let _promiseCapability_ be a new PromiseCapability { [[Promise]]: *undefined*, [[Resolve]]: *undefined*, [[Reject]]: *undefined* }.
          1. Let _executor_ be a new built-in function object as defined in <emu-xref href="#sec-getcapabilitiesexecutor-functions" title></emu-xref>.
          1. Set _executor_.[[Capability]] to _promiseCapability_.
          1. Let _promise_ be ? Construct(_C_, « _executor_ »).
          1. If IsCallable(_promiseCapability_.[[Resolve]]) is *false*, throw a *TypeError* exception.
          1. If IsCallable(_promiseCapability_.[[Reject]]) is *false*, throw a *TypeError* exception.
          1. Set _promiseCapability_.[[Promise]] to _promise_.
          1. Return _promiseCapability_.