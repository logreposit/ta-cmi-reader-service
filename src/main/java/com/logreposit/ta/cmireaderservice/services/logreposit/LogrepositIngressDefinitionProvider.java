package com.logreposit.ta.cmireaderservice.services.logreposit;

import com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.DataType;
import com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.definition.FieldDefinition;
import com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.definition.IngressDefinition;
import com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.definition.MeasurementDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class LogrepositIngressDefinitionProvider
{
    public IngressDefinition getIngressDefinition() {
        final var valueField = new FieldDefinition("value", DataType.FLOAT, "value");
        final var tags = Set.of("number", "signal", "unit");

        return new IngressDefinition(
                List.of(
                        new MeasurementDefinition(
                                "input",
                                tags,
                                List.of(
                                        valueField,
                                        new FieldDefinition("ras_state", DataType.STRING, "RAS state")
                                )
                        ),
                        new MeasurementDefinition(
                                "output",
                                tags,
                                List.of(
                                        valueField,
                                        new FieldDefinition("state", DataType.INTEGER, "state")
                                )
                        ),
                        new MeasurementDefinition("analog_logging", tags, List.of(valueField)),
                        new MeasurementDefinition("digital_logging", tags, List.of(valueField)),
                        new MeasurementDefinition("analog_network", tags, List.of(valueField)),
                        new MeasurementDefinition("digital_network", tags, List.of(valueField))
                )
        );
    }
}
