package com.HOIIVUtils.clauzewitz.code.effect;

import com.HOIIVUtils.clauzewitz.code.scope.ScopeType;

import java.util.EnumSet;
import java.util.List;

public record EffectSchema(List<String> pdxIdentifiers, EnumSet<ScopeType> supportedScopes,
                           EnumSet<ScopeType> supportedTargets) {

    public EffectSchema(String pdxIdentifier, EnumSet<ScopeType> supportedScopes) {
        this(List.of(pdxIdentifier), supportedScopes, null);
    }
}
