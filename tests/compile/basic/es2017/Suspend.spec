          1. Assert: The calling agent is in the critical section for _WL_.
          1. Assert: _W_ is equal to AgentSignifier().
          1. Assert: _W_ is on the list of waiters in _WL_.
          1. Assert: AgentCanSuspend() is *true*.
          1. Perform LeaveCriticalSection(_WL_) and suspend _W_ for up to _timeout_ milliseconds, performing the combined operation in such a way that a wakeup that arrives after the critical section is exited but before the suspension takes effect is not lost.  _W_ can wake up either because the timeout expired or because it was woken explicitly by another agent calling WakeWaiter(_WL_, _W_), and not for any other reasons at all.
          1. Perform EnterCriticalSection(_WL_).
          1. If _W_ was woken explicitly by another agent calling WakeWaiter(_WL_, _W_), return *true*.
          1. Return *false*.