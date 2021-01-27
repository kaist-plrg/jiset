            1. Let _strLen_ be the number of code units in _string_.
            1. Let _R_ be the empty String.
            1. Let _k_ be 0.
            1. Repeat,
              1. If _k_ equals _strLen_, return _R_.
              1. Let _C_ be the code unit at index _k_ within _string_.
              1. If _C_ is in _unescapedSet_, then
                1. Let _S_ be the String value containing only the code unit _C_.
                1. Set _R_ to the string-concatenation of the previous value of _R_ and _S_.
              1. Else _C_ is not in _unescapedSet_,
                1. If _C_ is a <emu-xref href="#trailing-surrogate"></emu-xref>, throw a *URIError* exception.
                1. If _C_ is not a <emu-xref href="#leading-surrogate"></emu-xref>, then
                  1. Let _V_ be the code point with the same numeric value as code unit _C_.
                1. Else,
                  1. Increase _k_ by 1.
                  1. If _k_ equals _strLen_, throw a *URIError* exception.
                  1. Let _kChar_ be the code unit at index _k_ within _string_.
                  1. If _kChar_ is not a <emu-xref href="#trailing-surrogate"></emu-xref>, throw a *URIError* exception.
                  1. Let _V_ be UTF16Decode(_C_, _kChar_).
                1. Let _Octets_ be the List of octets resulting by applying the UTF-8 transformation to _V_.
                1. For each element _octet_ of _Octets_ in List order, do
                  1. Let _S_ be the string-concatenation of:
                    * `"%"`
                    * the String representation of _octet_, formatted as a two-digit uppercase hexadecimal number, padded to the left with a zero if necessary
                  1. Set _R_ to the string-concatenation of the previous value of _R_ and _S_.
              1. Increase _k_ by 1.