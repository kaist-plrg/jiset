        1. If _result_ is an abrupt completion, perform HostReportErrors(« _result_.[[Value]] »).
        1. Suspend the running execution context and remove it from the execution context stack.
        1. Assert: The execution context stack is now empty.
        1. Let _nextQueue_ be a non-empty Job Queue chosen in an implementation defined manner. If all Job Queues are empty, the result is implementation defined.
        1. Let _nextPending_ be the PendingJob record at the front of _nextQueue_. Remove that record from _nextQueue_.
        1. Let _newContext_ be a new execution context.
        1. Set _newContext_'s Function to *null*.
        1. Set _newContext_'s Realm to _nextPending_.[[Realm]].
        1. Set _newContext_'s ScriptOrModule to _nextPending_.[[ScriptOrModule]].
        1. Push _newContext_ onto the execution context stack; _newContext_ is now the running execution context.
        1. Perform any implementation or host environment defined job initialization using _nextPending_.
        1. Perform the abstract operation named by _nextPending_.[[Job]] using the elements of _nextPending_.[[Arguments]] as its arguments.