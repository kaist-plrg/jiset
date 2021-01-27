        1. Let _leafContext_ be the running execution context.
        1. Suspend _leafContext_.
        1. Pop _leafContext_ from the execution context stack. The execution context now on the top of the stack becomes the running execution context.
        1. Assert: _leafContext_ has no further use. It will never be activated as the running execution context.