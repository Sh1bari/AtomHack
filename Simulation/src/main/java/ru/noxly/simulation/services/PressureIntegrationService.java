package ru.noxly.simulation.services;

import lombok.*;
import org.springframework.stereotype.Service;
import ru.noxly.simulation.models.entities.redis.Reservoir;
import ru.noxly.simulation.repositories.RepoResolver;
import ru.noxly.simulation.repositories.redis.ReservoirRedisRepository;
import ru.noxly.simulation.webClients.dyndns.DynDnsClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PressureIntegrationService {

    private final DynDnsClient dynDnsClient;
    private final RepoResolver repoResolver;
    private final ReservoirRedisRepository reservoirRepository;

    public void postgresUpdater() {
        Map<Long, Double> pressureMap = extractPressureList();
        saveToPostgres(pressureMap);
        saveToRedis(pressureMap);
    }

    public void redisUpdater() {
        Map<Long, Double> pressureMap = extractPressureList();
        saveToRedis(pressureMap);
    }

    private void saveToPostgres(Map<Long, Double> pressureMap) {
        List<Long> keysList = new ArrayList<>(pressureMap.keySet());
        List<Double> pressureSet = new ArrayList<>(pressureMap.values());
        repoResolver.getReservoirRepository().updatePressure(keysList, pressureSet);
    }

    private void saveToRedis(Map<Long, Double> pressureMap) {
        pressureMap.forEach((key, value) -> {
            val reservoir = Reservoir.init()
                    .setId(key)
                    .setPressure(value)
                    .build();
            reservoirRepository.save(reservoir);
        });
    }

    private Map<Long, Double> extractPressureList() {
        Map<Long, Double> pressureMap = new HashMap<>();
        for (long id = 1; id <= 10; id++) {
            val pressure = dynDnsClient.getPressure(id);
            pressureMap.put(id, pressure);
        }

        return pressureMap;
    }
}