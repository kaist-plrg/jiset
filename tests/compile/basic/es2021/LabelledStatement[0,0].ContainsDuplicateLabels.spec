        1. Let _label_ be the StringValue of |LabelIdentifier|.
        1. If _label_ is an element of _labelSet_, return *true*.
        1. Let _newLabelSet_ be a copy of _labelSet_ with _label_ appended.
        1. Return ContainsDuplicateLabels of |LabelledItem| with argument _newLabelSet_.