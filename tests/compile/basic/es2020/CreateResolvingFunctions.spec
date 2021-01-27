          1. Let _alreadyResolved_ be the Record { [[Value]]: *false* }.
          1. Let _stepsResolve_ be the algorithm steps defined in <emu-xref href="#sec-promise-resolve-functions" title></emu-xref>.
          1. Let _resolve_ be ! CreateBuiltinFunction(_stepsResolve_, « [[Promise]], [[AlreadyResolved]] »).
          1. Set _resolve_.[[Promise]] to _promise_.
          1. Set _resolve_.[[AlreadyResolved]] to _alreadyResolved_.
          1. Let _stepsReject_ be the algorithm steps defined in <emu-xref href="#sec-promise-reject-functions" title></emu-xref>.
          1. Let _reject_ be ! CreateBuiltinFunction(_stepsReject_, « [[Promise]], [[AlreadyResolved]] »).
          1. Set _reject_.[[Promise]] to _promise_.
          1. Set _reject_.[[AlreadyResolved]] to _alreadyResolved_.
          1. Return the Record { [[Resolve]]: _resolve_, [[Reject]]: _reject_ }.