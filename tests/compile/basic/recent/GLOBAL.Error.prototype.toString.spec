          1. Let _O_ be the *this* value.
          1. If Type(_O_) is not Object, throw a *TypeError* exception.
          1. Let _name_ be ? Get(_O_, *"name"*).
          1. If _name_ is *undefined*, set _name_ to *"Error"*; otherwise set _name_ to ? ToString(_name_).
          1. Let _msg_ be ? Get(_O_, *"message"*).
          1. If _msg_ is *undefined*, set _msg_ to the empty String; otherwise set _msg_ to ? ToString(_msg_).
          1. If _name_ is the empty String, return _msg_.
          1. If _msg_ is the empty String, return _name_.
          1. Return the string-concatenation of _name_, the code unit 0x003A (COLON), the code unit 0x0020 (SPACE), and _msg_.