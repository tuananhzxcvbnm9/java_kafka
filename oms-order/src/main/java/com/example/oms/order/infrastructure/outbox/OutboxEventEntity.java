package com.example.oms.order.infrastructure.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events", indexes = @Index(name = "idx_outbox_status_created", columnList = "status,createdAt"))
public class OutboxEventEntity {
    @Id
    private UUID id;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private Integer eventVersion;
    @Column(columnDefinition = "TEXT")
    private String payloadJson;
    private String status;
    private Instant createdAt;
    private Instant publishedAt;
}
