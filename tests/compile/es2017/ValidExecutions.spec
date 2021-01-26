        1. The host provides a host-synchronizes-with Relation for _execution_.[[HostSynchronizesWith]], and
        1. _execution_.[[HappensBefore]] is a strict partial order, and
        1. _execution_ has valid chosen reads, and
        1. _execution_ has coherent reads, and
        1. _execution_ has tear free reads, and
        1. _execution_ has sequentially consistent atomics.