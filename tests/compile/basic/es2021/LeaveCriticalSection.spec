          1. Assert: The calling agent is in the critical section for _WL_.
          1. Let _execution_ be the [[CandidateExecution]] field of the calling surrounding's Agent Record.
          1. Let _eventsRecord_ be the Agent Events Record in _execution_.[[EventsRecords]] whose [[AgentSignifier]] is AgentSignifier().
          1. Let _leaverEventList_ be _eventsRecord_.[[EventList]].
          1. Let _leaveEvent_ be a new Synchronize event.
          1. Append _leaveEvent_ to _leaverEventList_.
          1. Set the Synchronize event in _WL_ to _leaveEvent_.
          1. Leave the critical section for _WL_.