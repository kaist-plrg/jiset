        1. Let _handler_ be _O_.[[ProxyHandler]].
        1. If _handler_ is *null*, throw a *TypeError* exception.
        1. Assert: Type(_handler_) is Object.
        1. Let _target_ be _O_.[[ProxyTarget]].
        1. Let _trap_ be ? GetMethod(_handler_, `"ownKeys"`).
        1. If _trap_ is *undefined*, then
          1. Return ? _target_.[[OwnPropertyKeys]]().
        1. Let _trapResultArray_ be ? Call(_trap_, _handler_, « _target_ »).
        1. Let _trapResult_ be ? CreateListFromArrayLike(_trapResultArray_, « String, Symbol »).
        1. If _trapResult_ contains any duplicate entries, throw a *TypeError* exception.
        1. Let _extensibleTarget_ be ? IsExtensible(_target_).
        1. Let _targetKeys_ be ? _target_.[[OwnPropertyKeys]]().
        1. Assert: _targetKeys_ is a List containing only String and Symbol values.
        1. Assert: _targetKeys_ contains no duplicate entries.
        1. Let _targetConfigurableKeys_ be a new empty List.
        1. Let _targetNonconfigurableKeys_ be a new empty List.
        1. For each element _key_ of _targetKeys_, do
          1. Let _desc_ be ? _target_.[[GetOwnProperty]](_key_).
          1. If _desc_ is not *undefined* and _desc_.[[Configurable]] is *false*, then
            1. Append _key_ as an element of _targetNonconfigurableKeys_.
          1. Else,
            1. Append _key_ as an element of _targetConfigurableKeys_.
        1. If _extensibleTarget_ is *true* and _targetNonconfigurableKeys_ is empty, then
          1. Return _trapResult_.
        1. Let _uncheckedResultKeys_ be a new List which is a copy of _trapResult_.
        1. For each _key_ that is an element of _targetNonconfigurableKeys_, do
          1. If _key_ is not an element of _uncheckedResultKeys_, throw a *TypeError* exception.
          1. Remove _key_ from _uncheckedResultKeys_.
        1. If _extensibleTarget_ is *true*, return _trapResult_.
        1. For each _key_ that is an element of _targetConfigurableKeys_, do
          1. If _key_ is not an element of _uncheckedResultKeys_, throw a *TypeError* exception.
          1. Remove _key_ from _uncheckedResultKeys_.
        1. If _uncheckedResultKeys_ is not empty, throw a *TypeError* exception.
        1. Return _trapResult_.