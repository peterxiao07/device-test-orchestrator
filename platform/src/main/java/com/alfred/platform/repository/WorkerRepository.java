package com.alfred.platform.repository;

import com.alfred.platform.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    Optional<Worker> findByWorkerCode(String workerCode);
}
