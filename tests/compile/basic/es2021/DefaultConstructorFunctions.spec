          1. If NewTarget is *undefined*, throw a *TypeError* exception.
          1. Let _F_ be the active function object.
          1. If _F_.[[ConstructorKind]] is ~derived~, then
            1. NOTE: This branch behaves similarly to `constructor(...args) { super(...args); }`. The most notable distinction is that while the aforementioned ECMAScript source text observably calls the @@iterator method on `%Array.prototype%`, a Default Constructor Function does not.
            1. Let _func_ be ! _F_.[[GetPrototypeOf]]().
            1. If IsConstructor(_func_) is *false*, throw a *TypeError* exception.
            1. Return ? Construct(_func_, _args_, NewTarget).
          1. Else,
            1. NOTE: This branch behaves similarly to `constructor() {}`.
            1. Return ? OrdinaryCreateFromConstructor(NewTarget, *"%Object.prototype%"*).