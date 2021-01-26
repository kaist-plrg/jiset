        1. Assert: IsPropertyKey(_P_) is *true*.
        1. Let _handler_ be the value of the [[ProxyHandler]] internal slot of _O_.
        1. If _handler_ is *null*, throw a *TypeError* exception.
        1. Assert: Type(_handler_) is Object.
        1. Let _target_ be the value of the [[ProxyTarget]] internal slot of _O_.
        1. Let _trap_ be ? GetMethod(_handler_, `"defineProperty"`).
        1. If _trap_ is *undefined*, then
          1. Return ? _target_.[[DefineOwnProperty]](_P_, _Desc_).
        1. Let _descObj_ be FromPropertyDescriptor(_Desc_).
        1. Let _booleanTrapResult_ be ToBoolean(? Call(_trap_, _handler_, « _target_, _P_, _descObj_ »)).
        1. If _booleanTrapResult_ is *false*, return *false*.
        1. Let _targetDesc_ be ? _target_.[[GetOwnProperty]](_P_).
        1. Let _extensibleTarget_ be ? IsExtensible(_target_).
        1. If _Desc_ has a [[Configurable]] field and if _Desc_.[[Configurable]] is *false*, then
          1. Let _settingConfigFalse_ be *true*.
        1. Else let _settingConfigFalse_ be *false*.
        1. If _targetDesc_ is *undefined*, then
          1. If _extensibleTarget_ is *false*, throw a *TypeError* exception.
          1. If _settingConfigFalse_ is *true*, throw a *TypeError* exception.
        1. Else _targetDesc_ is not *undefined*,
          1. If IsCompatiblePropertyDescriptor(_extensibleTarget_, _Desc_, _targetDesc_) is *false*, throw a *TypeError* exception.
          1. If _settingConfigFalse_ is *true* and _targetDesc_.[[Configurable]] is *true*, throw a *TypeError* exception.
        1. Return *true*.