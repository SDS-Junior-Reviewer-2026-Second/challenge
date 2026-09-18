package org.example.assemble;

import java.util.Optional;

@FunctionalInterface
public interface CompatibilityRule {
    Optional<String> findViolation(VehicleConfiguration configuration);
}
