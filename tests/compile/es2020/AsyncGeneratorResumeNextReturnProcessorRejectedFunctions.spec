            1. Let _F_ be the active function object.
            1. Set _F_.[[Generator]].[[AsyncGeneratorState]] to ~completed~.
            1. Return ! AsyncGeneratorReject(_F_.[[Generator]], _reason_).