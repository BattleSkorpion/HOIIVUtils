package clausewitz_coding.code.trigger;

import clausewitz_coding.code.scope.ScopeTargetType;

import java.util.EnumSet;

public interface Trigger {
	/**
	 * Return scope target type, different triggers can only be used within certain scopes.
	 */
	ScopeTargetType scopeTargetType();

	EnumSet<TriggerParameter> parameters();


}
