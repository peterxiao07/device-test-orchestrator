package com.alfred.platform.service;

import com.alfred.platform.entity.Worker;
import com.alfred.platform.repository.WorkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
public class WorkerService {
    private final WorkerRepository workerRepository;
    private final Clock clock;

    /**
     * I use constructor injection to make dependencies explicit and simplify testing.
     * 我使用构造方法注入，让依赖明确，并简化测试。
     */
//    Constructor injection（构造方法注入）
    public WorkerService(WorkerRepository workerRepository, Clock clock) {
        this.workerRepository = workerRepository;
        this.clock = clock;
    }

//    Transaction boundary（事务边界）
    @Transactional
    public RegistrationResult register(String workerCode, String baseUrl) {
        Instant now = clock.instant();
        Optional<Worker> existingWorker =
                workerRepository.findByWorkerCode(workerCode);

        if (existingWorker.isPresent()) {
            Worker worker = existingWorker.get();
            worker.refreshRegistration(baseUrl, now);

            Worker savedWorker = workerRepository.save(worker);
            return toResult(savedWorker, false);
        }

        Worker worker = new Worker(workerCode, baseUrl, now);
        Worker savedWorker = workerRepository.save(worker);

        return toResult(savedWorker, true);
    }

    private RegistrationResult toResult(Worker worker, boolean created) {
        return new RegistrationResult(
                worker.getId(),
                worker.getWorkerCode(),
                worker.getBaseUrl(),
                created
        );
    }

    public record RegistrationResult(
            Long id,
            String workerCode,
            String baseUrl,
            boolean created
    ) {
    }
}