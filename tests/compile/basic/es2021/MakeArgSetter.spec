            1. Let _steps_ be the steps of an ArgSetter function as specified below.
            1. Let _length_ be the number of non-optional parameters of an ArgSetter function as specified below.
            1. Let _setter_ be ! CreateBuiltinFunction(_steps_, _length_, *""*, « [[Name]], [[Env]] »).
            1. Set _setter_.[[Name]] to _name_.
            1. Set _setter_.[[Env]] to _env_.
            1. Return _setter_.