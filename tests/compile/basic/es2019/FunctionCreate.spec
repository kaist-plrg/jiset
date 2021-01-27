        1. If _prototype_ is not present, then
          1. Set _prototype_ to the intrinsic object %FunctionPrototype%.
        1. If _kind_ is not ~Normal~, let _allocKind_ be `"non-constructor"`.
        1. Else, let _allocKind_ be `"normal"`.
        1. Let _F_ be FunctionAllocate(_prototype_, _Strict_, _allocKind_).
        1. Return FunctionInitialize(_F_, _kind_, _ParameterList_, _Body_, _Scope_).