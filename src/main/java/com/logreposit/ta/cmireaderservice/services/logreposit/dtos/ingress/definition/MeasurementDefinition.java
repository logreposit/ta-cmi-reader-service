package com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.definition;

import java.util.List;
import java.util.Set;

public record MeasurementDefinition(
        String name,
        Set<String> tags,
        List<FieldDefinition> fields) {}
