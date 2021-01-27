          1. For each _reaction_ in _reactions_, in original insertion order, do
            1. Let _job_ be NewPromiseReactionJob(_reaction_, _argument_).
            1. Perform HostEnqueuePromiseJob(_job_.[[Job]], _job_.[[Realm]]).
          1. Return *undefined*.