          1. Let _uriString_ be ? ToString(_uri_).
          1. Let _unescapedURISet_ be a String containing one instance of each code unit valid in |uriReserved| and |uriUnescaped| plus `"#"`.
          1. Return ? Encode(_uriString_, _unescapedURISet_).