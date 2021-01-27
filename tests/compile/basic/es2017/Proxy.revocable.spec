          1. Let _p_ be ? ProxyCreate(_target_, _handler_).
          1. Let _revoker_ be a new built-in function object as defined in <emu-xref href="#sec-proxy-revocation-functions"></emu-xref>.
          1. Set _revoker_.[[RevocableProxy]] to _p_.
          1. Let _result_ be ObjectCreate(%ObjectPrototype%).
          1. Perform CreateDataProperty(_result_, `"proxy"`, _p_).
          1. Perform CreateDataProperty(_result_, `"revoke"`, _revoker_).
          1. Return _result_.