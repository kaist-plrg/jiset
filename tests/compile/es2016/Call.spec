        1. If _argumentsList_ was not passed, let _argumentsList_ be a new empty List.
        1. If IsCallable(_F_) is *false*, throw a *TypeError* exception.
        1. Return ? _F_.[[Call]](_V_, _argumentsList_).