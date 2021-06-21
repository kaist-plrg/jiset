        1. Let _realmRec_ be a new Realm Record.
        1. Perform CreateIntrinsics(_realmRec_).
        1. Set _realmRec_.[[GlobalObject]] to *undefined*.
        1. Set _realmRec_.[[GlobalEnv]] to *undefined*.
        1. Set _realmRec_.[[TemplateMap]] to a new empty List.
        1. Return _realmRec_.