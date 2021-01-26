        1. For each ReadSharedMemory or ReadModifyWriteSharedMemory event _R_ in SharedDataBlockEventSet(_execution_), do
          1. Let _chosenValue_ be the element of _execution_.[[ChosenValues]] whose [[Event]] field is _R_.
          1. Let _readValue_ be ValueOfReadEvent(_execution_, _R_).
          1. Let _chosenLen_ be the number of elements of _chosenValue_.
          1. Let _readLen_ be the number of elements of _readValue_.
          1. If _chosenLen_ is not equal to _readLen_, then
            1. Return *false*.
          1. If _chosenValue_[_i_] is not equal to _readValue_[_i_] for any integer value _i_ in the range 0 through _chosenLen_, exclusive, then
            1. Return *false*.
          1. Return *true*.