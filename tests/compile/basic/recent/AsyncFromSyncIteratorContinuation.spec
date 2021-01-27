          1. Let _done_ be IteratorComplete(_result_).
          1. IfAbruptRejectPromise(_done_, _promiseCapability_).
          1. Let _value_ be IteratorValue(_result_).
          1. IfAbruptRejectPromise(_value_, _promiseCapability_).
          1. Let _valueWrapper_ be PromiseResolve(%Promise%, _value_).
          1. IfAbruptRejectPromise(_valueWrapper_, _promiseCapability_).
          1. Let _steps_ be the algorithm steps defined in <emu-xref href="#sec-async-from-sync-iterator-value-unwrap-functions" title></emu-xref>.
          1. Let _onFulfilled_ be ! CreateBuiltinFunction(_steps_, « [[Done]] »).
          1. Set _onFulfilled_.[[Done]] to _done_.
          1. Perform ! PerformPromiseThen(_valueWrapper_, _onFulfilled_, *undefined*, _promiseCapability_).
          1. Return _promiseCapability_.[[Promise]].