          1. Let _referencingScriptOrModule_ be ! GetActiveScriptOrModule().
          1. Let _argRef_ be the result of evaluating |AssignmentExpression|.
          1. Let _specifier_ be ? GetValue(_argRef_).
          1. Let _promiseCapability_ be ! NewPromiseCapability(%Promise%).
          1. Let _specifierString_ be ToString(_specifier_).
          1. IfAbruptRejectPromise(_specifierString_, _promiseCapability_).
          1. Perform ! HostImportModuleDynamically(_referencingScriptOrModule_, _specifierString_, _promiseCapability_).
          1. Return _promiseCapability_.[[Promise]].