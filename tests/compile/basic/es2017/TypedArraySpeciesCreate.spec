          1. Assert: _exemplar_ is an Object that has a [[TypedArrayName]] internal slot.
          1. Let _defaultConstructor_ be the intrinsic object listed in column one of <emu-xref href="#table-49"></emu-xref> for _exemplar_.[[TypedArrayName]].
          1. Let _constructor_ be ? SpeciesConstructor(_exemplar_, _defaultConstructor_).
          1. Return ? TypedArrayCreate(_constructor_, _argumentList_).