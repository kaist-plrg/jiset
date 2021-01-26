          1. Assert: Either Type(_V_) is Object or Type(_V_) is Null.
          1. Let _extensible_ be the value of the [[Extensible]] internal slot of _O_.
          1. Let _current_ be the value of the [[Prototype]] internal slot of _O_.
          1. If SameValue(_V_, _current_) is *true*, return *true*.
          1. If _extensible_ is *false*, return *false*.
          1. Let _p_ be _V_.
          1. Let _done_ be *false*.
          1. Repeat while _done_ is *false*,
            1. If _p_ is *null*, let _done_ be *true*.
            1. Else, if SameValue(_p_, _O_) is *true*, return *false*.
            1. Else,
              1. If the [[GetPrototypeOf]] internal method of _p_ is not the ordinary object internal method defined in <emu-xref href="#sec-ordinary-object-internal-methods-and-internal-slots-getprototypeof"></emu-xref>, let _done_ be *true*.
              1. Else, let _p_ be the value of _p_'s [[Prototype]] internal slot.
          1. Set the value of the [[Prototype]] internal slot of _O_ to _V_.
          1. Return *true*.