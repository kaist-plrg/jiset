        1. Let _buffer_ be ? ValidateIntegerTypedArray(_typedArray_).
        1. Let _indexedPosition_ be ? ValidateAtomicAccess(_typedArray_, _index_).
        1. If IsDetachedBuffer(_buffer_) is *true*, throw a *TypeError* exception.
        1. NOTE: The above check is not redundant with the check in ValidateIntegerTypedArray because the call to ValidateAtomicAccess on the preceding line can have arbitrary side effects, which could cause the buffer to become detached.
        1. Let _arrayTypeName_ be _typedArray_.[[TypedArrayName]].
        1. Let _elementType_ be the Element Type value in <emu-xref href="#table-the-typedarray-constructors"></emu-xref> for _arrayTypeName_.
        1. Return GetValueFromBuffer(_buffer_, _indexedPosition_, _elementType_, *true*, ~SeqCst~).