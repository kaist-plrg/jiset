          1. Let _resolvingFunctions_ be CreateResolvingFunctions(_promiseToResolve_).
          1. Let _thenCallResult_ be Call(_then_, _thenable_, « _resolvingFunctions_.[[Resolve]], _resolvingFunctions_.[[Reject]] »).
          1. If _thenCallResult_ is an abrupt completion, then
            1. Let _status_ be Call(_resolvingFunctions_.[[Reject]], *undefined*, « _thenCallResult_.[[Value]] »).
            1. Return Completion(_status_).
          1. Return Completion(_thenCallResult_).