

# V1alpha1ValidatingAdmissionPolicySpec

ValidatingAdmissionPolicySpec is the specification of the desired behavior of the AdmissionPolicy.
## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**auditAnnotations** | [**List&lt;V1alpha1AuditAnnotation&gt;**](V1alpha1AuditAnnotation.md) | auditAnnotations contains CEL expressions which are used to produce audit annotations for the audit event of the API request. validations and auditAnnotations may not both be empty; a least one of validations or auditAnnotations is required. |  [optional]
**failurePolicy** | **String** | failurePolicy defines how to handle failures for the admission policy. Failures can occur from CEL expression parse errors, type check errors, runtime errors and invalid or mis-configured policy definitions or bindings.  A policy is invalid if spec.paramKind refers to a non-existent Kind. A binding is invalid if spec.paramRef.name refers to a non-existent resource.  failurePolicy does not define how validations that evaluate to false are handled.  When failurePolicy is set to Fail, ValidatingAdmissionPolicyBinding validationActions define how failures are enforced.  Allowed values are Ignore or Fail. Defaults to Fail. |  [optional]
**matchConditions** | [**List&lt;V1alpha1MatchCondition&gt;**](V1alpha1MatchCondition.md) | MatchConditions is a list of conditions that must be met for a request to be validated. Match conditions filter requests that have already been matched by the rules, namespaceSelector, and objectSelector. An empty list of matchConditions matches all requests. There are a maximum of 64 match conditions allowed.  If a parameter object is provided, it can be accessed via the &#x60;params&#x60; handle in the same manner as validation expressions.  The exact matching logic is (in order):   1. If ANY matchCondition evaluates to FALSE, the policy is skipped.   2. If ALL matchConditions evaluate to TRUE, the policy is evaluated.   3. If any matchCondition evaluates to an error (but none are FALSE):      - If failurePolicy&#x3D;Fail, reject the request      - If failurePolicy&#x3D;Ignore, the policy is skipped |  [optional]
**matchConstraints** | [**V1alpha1MatchResources**](V1alpha1MatchResources.md) |  |  [optional]
**paramKind** | [**V1alpha1ParamKind**](V1alpha1ParamKind.md) |  |  [optional]
**validations** | [**List&lt;V1alpha1Validation&gt;**](V1alpha1Validation.md) | Validations contain CEL expressions which is used to apply the validation. Validations and AuditAnnotations may not both be empty; a minimum of one Validations or AuditAnnotations is required. |  [optional]
**variables** | [**List&lt;V1alpha1Variable&gt;**](V1alpha1Variable.md) | Variables contain definitions of variables that can be used in composition of other expressions. Each variable is defined as a named CEL expression. The variables defined here will be available under &#x60;variables&#x60; in other expressions of the policy except MatchConditions because MatchConditions are evaluated before the rest of the policy.  The expression of a variable can refer to other variables defined earlier in the list but not those after. Thus, Variables must be sorted by the order of first appearance and acyclic. |  [optional]



