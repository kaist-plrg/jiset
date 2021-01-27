          1. Assert: The calling agent is in the critical section for _WL_.
          1. Assert: _W_ is on the list of waiters in _WL_.
          1. Let _execution_ be the [[CandidateExecution]] field of the surrounding agent's Agent Record.
          1. Let _eventsRecord_ be the Agent Events Record in _execution_.[[EventsRecords]] whose [[AgentSignifier]] is AgentSignifier().
          1. Let _agentSynchronizesWith_ be _eventsRecord_.[[AgentSynchronizesWith]].
          1. Let _notifierEventList_ be _eventsRecord_.[[EventList]].
          1. Let _waiterEventList_ be the [[EventList]] field of the element in _execution_.[[EventsRecords]] whose [[AgentSignifier]] is _W_.
          1. Let _notifyEvent_ and _waitEvent_ be new Synchronize events.
          1. Append _notifyEvent_ to _notifierEventList_.
          1. Append _waitEvent_ to _waiterEventList_.
          1. Append (_notifyEvent_, _waitEvent_) to _agentSynchronizesWith_.
          1. Notify the agent _W_.