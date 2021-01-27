          1. Assert: _exemplar_ is an Object that has [[TypedArrayName]] and [[ContentType]] internal slots.
          1. Let _defaultConstructor_ be the intrinsic object listed in column one of <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _exemplar_.[[TypedArrayName]].
          1. Let _constructor_ be ? SpeciesConstructor(_exemplar_, _defaultConstructor_).
          1. Let _result_ be ? TypedArrayCreate(_constructor_, _argumentList_).
          1. Assert: _result_ has [[TypedArrayName]] and [[ContentType]] internal slots.
          1. If _result_.[[ContentType]] is not equal to _exemplar_.[[ContentType]], throw a *TypeError* exception.
          1. Return _result_.