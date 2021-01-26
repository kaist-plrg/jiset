            1. Let _envRec_ be the global Environment Record for which the method was invoked.
            1. Let _varDeclaredNames_ be _envRec_.[[VarNames]].
            1. If _varDeclaredNames_ contains _N_, return *true*.
            1. Return *false*.