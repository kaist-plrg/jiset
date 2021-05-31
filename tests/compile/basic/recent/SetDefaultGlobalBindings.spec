        1. Let _global_ be _realmRec_.[[GlobalObject]].
        1. For each property of the Global Object specified in clause <emu-xref href="#sec-global-object"></emu-xref>, do
          1. Let _name_ be the String value of the property name.
          1. Let _desc_ be the fully populated data Property Descriptor for the property, containing the specified attributes for the property. For properties listed in <emu-xref href="#sec-function-properties-of-the-global-object"></emu-xref>, <emu-xref href="#sec-constructor-properties-of-the-global-object"></emu-xref>, or <emu-xref href="#sec-other-properties-of-the-global-object"></emu-xref> the value of the [[Value]] attribute is the corresponding intrinsic object from _realmRec_.
          1. Perform ? DefinePropertyOrThrow(_global_, _name_, _desc_).
        1. Return _global_.