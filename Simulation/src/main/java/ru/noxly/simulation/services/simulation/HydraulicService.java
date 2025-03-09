package ru.noxly.simulation.services.simulation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.noxly.simulation.models.entities.Pipe;
import ru.noxly.simulation.models.entities.Reservoir;
import ru.noxly.simulation.models.entities.Space;
import ru.noxly.simulation.models.models.dtos.ReservoirDto;
import ru.noxly.simulation.redis.ReservoirPublisher;
import ru.noxly.simulation.repositories.RepoResolver;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HydraulicService {

    private final RepoResolver repoResolver;

    private final SimulationProcessor simulationProcessor;

    /**
     * Запускается каждые 5 секунд.
     * Выполняет N_STEPS итераций гидравлической модели.
     */
    public void simulate() {
        val spaces = getSpaces();

        List<CompletableFuture<Void>> futures = spaces.stream()
                .map(this::runSimulationAsync)
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allFutures.get();
            log.info("Все симуляции завершены");
        } catch (InterruptedException | ExecutionException e) {
            log.error("Ошибка ожидания завершения симуляций", e);
            Thread.currentThread().interrupt();
        }
    }

    private CompletableFuture<Void> runSimulationAsync(Space space) {
        return CompletableFuture.runAsync(() -> {
            try {
                simulationProcessor.simulateForSpace(space);
            } catch (Exception e) {
                log.error("Ошибка симуляции для пространства {}: {}", space.getId(), e.getMessage(), e);
            }
        }).exceptionally(ex -> {
            log.error("Непредвиденная ошибка в симуляции пространства {}: {}", space.getId(), ex.getMessage(), ex);
            return null;
        });
    }

    private List<Space> getSpaces() {
        return repoResolver.resolve(Space.class)
                .findAll(Specification.where(null));
    }
}
