          1. Let _job_ be a new Job abstract closure with no parameters that captures _promiseToResolve_, _thenable_, and _then_ and performs the following steps when called:
            1. Let _resolvingFunctions_ be CreateResolvingFunctions(_promiseToResolve_).
            1. Let _thenCallResult_ be Call(_then_, _thenable_, « _resolvingFunctions_.[[Resolve]], _resolvingFunctions_.[[Reject]] »).
            1. If _thenCallResult_ is an abrupt completion, then
              1. Let _status_ be Call(_resolvingFunctions_.[[Reject]], *undefined*, « _thenCallResult_.[[Value]] »).
              1. Return Completion(_status_).
            1. Return Completion(_thenCallResult_).
          1. Let _getThenRealmResult_ be GetFunctionRealm(_then_).
          1. If _getThenRealmResult_ is a normal completion, then let _thenRealm_ be _getThenRealmResult_.[[Value]].
          1. Otherwise, let _thenRealm_ be *null*.
          1. Return the Record { [[Job]]: _job_, [[Realm]]: _thenRealm_ }.