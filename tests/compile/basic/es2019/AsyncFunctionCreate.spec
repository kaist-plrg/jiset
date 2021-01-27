        1. Let _functionPrototype_ be the intrinsic object %AsyncFunctionPrototype%.
        2. Let _F_ be ! FunctionAllocate(_functionPrototype_, _Strict_, `"async"`).
        3. Return ! FunctionInitialize(_F_, _kind_, _parameters_, _body_, _Scope_).