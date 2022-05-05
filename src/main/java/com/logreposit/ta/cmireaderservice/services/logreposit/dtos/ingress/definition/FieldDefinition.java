package com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.definition;

import com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.DataType;

public record FieldDefinition(
    String   name,
    DataType datatype,
    String   description) {}
