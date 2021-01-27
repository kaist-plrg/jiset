          1. Let _uriString_ be ? ToString(_encodedURI_).
          1. Let _reservedURISet_ be a String containing one instance of each code unit valid in |uriReserved| plus `"#"`.
          1. Return ? Decode(_uriString_, _reservedURISet_).