            1. Let _strLen_ be the number of code units in _string_.
            1. Let _R_ be the empty String.
            1. Let _k_ be 0.
            1. Repeat
              1. If _k_ equals _strLen_, return _R_.
              1. Let _C_ be the code unit at index _k_ within _string_.
              1. If _C_ is in _unescapedSet_, then
                1. Let _S_ be a String containing only the code unit _C_.
                1. Let _R_ be a new String value computed by concatenating the previous value of _R_ and _S_.
              1. Else _C_ is not in _unescapedSet_,
                1. If the code unit value of _C_ is not less than 0xDC00 and not greater than 0xDFFF, throw a *URIError* exception.
                1. If the code unit value of _C_ is less than 0xD800 or greater than 0xDBFF, then
                  1. Let _V_ be the code unit value of _C_.
                1. Else,
                  1. Increase _k_ by 1.
                  1. If _k_ equals _strLen_, throw a *URIError* exception.
                  1. Let _kChar_ be the code unit value of the code unit at index _k_ within _string_.
                  1. If _kChar_ is less than 0xDC00 or greater than 0xDFFF, throw a *URIError* exception.
                  1. Let _V_ be UTF16Decode(_C_, _kChar_).
                1. Let _Octets_ be the array of octets resulting by applying the UTF-8 transformation to _V_, and let _L_ be the array size.
                1. Let _j_ be 0.
                1. Repeat, while _j_ < _L_
                  1. Let _jOctet_ be the value at index _j_ within _Octets_.
                  1. Let _S_ be a String containing three code units <code>"%<var>XY</var>"</code> where _XY_ are two uppercase hexadecimal digits encoding the value of _jOctet_.
                  1. Let _R_ be a new String value computed by concatenating the previous value of _R_ and _S_.
                  1. Increase _j_ by 1.
              1. Increase _k_ by 1.