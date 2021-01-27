            1. Let _envRec_ be the function Environment Record for which the method was invoked.
            1. If _envRec_.[[ThisBindingStatus]] is ~lexical~, return *false*.
            1. If _envRec_.[[HomeObject]] has the value *undefined*, return *false*; otherwise, return *true*.