          1. Assert: Either Type(_V_) is Object or Type(_V_) is Null.
          1. Let _current_ be _O_.[[Prototype]].
          1. If SameValue(_V_, _current_) is *true*, return *true*.
          1. Let _extensible_ be _O_.[[Extensible]].
          1. If _extensible_ is *false*, return *false*.
          1. Let _p_ be _V_.
          1. Let _done_ be *false*.
          1. Repeat, while _done_ is *false*,
            1. If _p_ is *null*, set _done_ to *true*.
            1. Else if SameValue(_p_, _O_) is *true*, return *false*.
            1. Else,
              1. If _p_.[[GetPrototypeOf]] is not the ordinary object internal method defined in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots-getprototypeof"></emu-xref>, set _done_ to *true*.
              1. Else, set _p_ to _p_.[[Prototype]].
          1. Set _O_.[[Prototype]] to _V_.
          1. Return *true*.