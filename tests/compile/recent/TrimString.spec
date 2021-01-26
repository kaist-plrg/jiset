            1. Let _str_ be ? RequireObjectCoercible(_string_).
            1. Let _S_ be ? ToString(_str_).
            1. If _where_ is ~start~, let _T_ be the String value that is a copy of _S_ with leading white space removed.
            1. Else if _where_ is ~end~, let _T_ be the String value that is a copy of _S_ with trailing white space removed.
            1. Else,
              1. Assert: _where_ is ~start+end~.
              1. Let _T_ be the String value that is a copy of _S_ with both leading and trailing white space removed.
            1. Return _T_.