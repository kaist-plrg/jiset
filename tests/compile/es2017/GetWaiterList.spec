          1. Assert: _block_ is a Shared Data Block.
          1. Assert: _i_ and _i_+3 are valid byte offsets within the memory of _block_.
          1. Assert: _i_ is divisible by 4.
          1. Return the WaiterList that is referenced by the pair (_block_, _i_).