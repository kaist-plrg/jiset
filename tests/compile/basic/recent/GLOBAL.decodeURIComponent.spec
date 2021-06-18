          1. Let _componentString_ be ? ToString(_encodedURIComponent_).
          1. Let _reservedURIComponentSet_ be the empty String.
          1. Return ? Decode(_componentString_, _reservedURIComponentSet_).