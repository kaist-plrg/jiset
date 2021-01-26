          1. Let _componentString_ be ? ToString(_uriComponent_).
          1. Let _unescapedURIComponentSet_ be a String containing one instance of each code unit valid in |uriUnescaped|.
          1. Return ? Encode(_componentString_, _unescapedURIComponentSet_).