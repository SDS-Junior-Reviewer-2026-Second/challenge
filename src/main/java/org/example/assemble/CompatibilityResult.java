package org.example.assemble;

import java.util.Objects;

public record CompatibilityResult(boolean valid, String failureReason) {
    public CompatibilityResult {
        Objects.requireNonNull(failureReason, "failureReason");
        if (valid && !failureReason.isEmpty()) {
            throw new IllegalArgumentException("A valid result cannot have a failure reason");
        }
        if (!valid && failureReason.isBlank()) {
            throw new IllegalArgumentException("An invalid result requires a failure reason");
        }
    }

    public static CompatibilityResult pass() {
        return new CompatibilityResult(true, "");
    }

    public static CompatibilityResult fail(String reason) {
        return new CompatibilityResult(false, reason);
    }
}
