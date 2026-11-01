package com.alfred.platform.service;

import com.alfred.platform.entity.Worker;
import com.alfred.platform.repository.WorkerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkerServiceTest {

    private static final Instant NOW =
            Instant.parse("2026-11-01T08:00:00Z");

    @Mock
    private WorkerRepository workerRepository;

    private WorkerService workerService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(NOW, ZoneOffset.UTC);
        workerService = new WorkerService(workerRepository, fixedClock);
    }

    @Test
    void shouldCreateWorkerWhenWorkerCodeDoesNotExist() {
        // Arrange: prepare the inputs and repository behavior.
        String workerCode = "worker-001";
        String baseUrl = "http://localhost:9090";


        //stubbing（预设模拟行为）
        when(workerRepository.findByWorkerCode(workerCode))
                .thenReturn(Optional.empty());

        when(workerRepository.save(any(Worker.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act: execute the business operation.
        WorkerService.RegistrationResult result =
                workerService.register(workerCode, baseUrl);

        // Assert: verify the result and the worker passed to persistence.
        assertTrue(result.created());
        assertEquals(workerCode, result.workerCode());
        assertEquals(baseUrl, result.baseUrl());

        ArgumentCaptor<Worker> captor =
                ArgumentCaptor.forClass(Worker.class);

        verify(workerRepository).save(captor.capture());

        Worker savedWorker = captor.getValue();
        assertEquals(workerCode, savedWorker.getWorkerCode());
        assertEquals(baseUrl, savedWorker.getBaseUrl());
        assertEquals(NOW, savedWorker.getCreatedAt());
        assertEquals(NOW, savedWorker.getLastHeartbeatAt());
    }

    @Test
    void shouldUpdateExistingWorkerWhenWorkerCodeAlreadyExists(){
        
    }
}