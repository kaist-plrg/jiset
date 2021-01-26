            1. Let _name_ be the value of _f_'s [[Name]] internal slot.
            1. Let _env_ be the value of _f_'s [[Env]] internal slot.
            1. Return _env_.SetMutableBinding(_name_, _value_, *false*).