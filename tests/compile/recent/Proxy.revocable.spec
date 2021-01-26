          1. Let _p_ be ? ProxyCreate(_target_, _handler_).
          1. Let _steps_ be the algorithm steps defined in <emu-xref href="#sec-proxy-revocation-functions" title></emu-xref>.
          1. Let _revoker_ be ! CreateBuiltinFunction(_steps_, « [[RevocableProxy]] »).
          1. Set _revoker_.[[RevocableProxy]] to _p_.
          1. Let _result_ be ! OrdinaryObjectCreate(%Object.prototype%).
          1. Perform ! CreateDataPropertyOrThrow(_result_, *"proxy"*, _p_).
          1. Perform ! CreateDataPropertyOrThrow(_result_, *"revoke"*, _revoker_).
          1. Return _result_.