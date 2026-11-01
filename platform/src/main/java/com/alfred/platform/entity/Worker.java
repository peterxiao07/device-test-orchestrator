package com.alfred.platform.entity;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "worker")
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "worker_code",
            nullable = false,
            updatable = false,
            length = 100
    )
    private String workerCode;

    @Column(name = "base_url", nullable = false, length = 255)
    private String baseUrl;

    @Column(name = "last_heartbeat_at")
    private Instant lastHeartbeatAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Worker() {
        // Required by JPA.
    }

    public Worker(String workerCode, String baseUrl, Instant registeredAt) {
        this.workerCode = requireText(workerCode, "workerCode", 100);
        this.baseUrl = requireText(baseUrl, "baseUrl", 255);

        Instant timestamp = Objects.requireNonNull(
                registeredAt, "registeredAt must not be null"
        );
        this.lastHeartbeatAt = timestamp;
        this.createdAt = timestamp;
    }

    public void refreshRegistration(String baseUrl, Instant receivedAt) {
        String validatedUrl = requireText(baseUrl, "baseUrl", 255);
        Instant timestamp = Objects.requireNonNull(
                receivedAt, "receivedAt must not be null"
        );

        this.baseUrl = validatedUrl;
        this.lastHeartbeatAt = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getWorkerCode() {
        return workerCode;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Instant getLastHeartbeatAt() {
        return lastHeartbeatAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private static String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(
                    field + " must not be blank or exceed " + maxLength + " characters"
            );
        }
        return value;
    }
}
