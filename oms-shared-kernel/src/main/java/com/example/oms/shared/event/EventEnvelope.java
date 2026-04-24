package com.example.oms.shared.event;

import java.time.Instant;

public record EventEnvelope(
        String eventId,
        String eventType,
        int eventVersion,
        String correlationId,
        Instant occurredAt,
        String payload
) {}
