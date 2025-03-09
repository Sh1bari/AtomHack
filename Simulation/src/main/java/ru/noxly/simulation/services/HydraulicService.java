package ru.noxly.simulation.services;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.noxly.simulation.models.entities.Pipe;
import ru.noxly.simulation.models.entities.Reservoir;
import ru.noxly.simulation.models.entities.Space;
import ru.noxly.simulation.models.models.dtos.ReservoirDto;
import ru.noxly.simulation.redis.ReservoirPublisher;
import ru.noxly.simulation.repositories.RepoResolver;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HydraulicService {

    // --------------- КОНСТАНТЫ ---------------
    private static final double G = 9.81;       // м/с²
    private static final double RHO = 1000;       // кг/м³

    private static final double DT = 0.1;        // шаг по времени (с)
    private static final int N_STEPS = 5;     // число итераций за один запуск

    // "Предельная" высота резервуара (10 м)
    private static final double MAX_LEVEL = 10.0;

    private final RepoResolver repoResolver;
    private final ReservoirPublisher reservoirPublisher;

    /**
     * Запускается каждые 5 секунд.
     * Выполняет N_STEPS итераций гидравлической модели.
     */
    @Scheduled(fixedRate = 5000)
    public void simulate() {
        // 1) Читаем данные из БД и формируем Reservoir/pipe
        SimulationData data = loadDataFromDB();

        // 2) Инициализируем массивы давлений/уровней
        double[] P = new double[data.reservoirs.size()];
        double[] h = new double[data.reservoirs.size()];
        initPressuresAndLevels(data, P, h);

        // 4) Делаем N_STEPS итераций
        for (int step = 0; step < N_STEPS; step++) {
            // Выполняем один шаг (flows, обновляем P,h)
            doOneStepOfSimulation(data, P, h);
            printAndPublish(P, h, data.reservoirs);
        }

        // 5) Сохраняем финальные результаты в БД
        saveResults(data.reservoirs, P, h);
    }

    // ------------------------------------------------
    // 1) ЧТЕНИЕ ДАННЫХ ИЗ БД
    // ------------------------------------------------
    private SimulationData loadDataFromDB() {
        // Берём Space (первый найденный)
        val space = repoResolver.resolve(Space.class)
                .findAll(Specification.where(null))
                .get(0);

        // Извлекаем резервуары
        List<Reservoir> resList = space.getReservoirs().stream()
                // Переводим pressure из кПа в Па
                .map(r -> r.toBuilder()
                        .setPressure(r.getPressure() * 1000.0)
                        .build()
                )
                .toList();
        List<Reservoir> reservoirs = new ArrayList<>(resList);

        // Извлекаем трубы, убирая дубликаты
        List<Pipe> pipes = reservoirs.stream()
                .map(Reservoir::getOutgoingPipes)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        return new SimulationData(reservoirs, pipes);
    }

    // ------------------------------------------------
    // 2) ИНИЦИАЛИЗАЦИЯ (давление, уровень)
    // ------------------------------------------------
    private void initPressuresAndLevels(SimulationData data, double[] P, double[] h) {
        for (int i = 0; i < data.reservoirs.size(); i++) {
            Reservoir r = data.reservoirs.get(i);
            // P[i] = избыточное газовое давление (Па)
            P[i] = r.getPressure();
            // h[i] = уровень воды (м)
            double level = r.getLevel();
            // Ограничиваем если нужно
            if (level > MAX_LEVEL) level = MAX_LEVEL;
            h[i] = level;
        }
    }

    // ------------------------------------------------
    // 3) ОДИН ШАГ РАСЧЁТА
    // ------------------------------------------------
    private void doOneStepOfSimulation(SimulationData data, double[] P, double[] h) {
        // 3.1) Считаем расходы
        double[] flows = calculateFlows(data, P, h);

        // 3.2) Массив для приращений dP
        double[] dP = new double[data.reservoirs.size()];
        Arrays.fill(dP, 0.0);

        // 3.3) Обновляем dP и уровни
        for (int k = 0; k < data.pipes.size(); k++) {
            Pipe pipe = data.pipes.get(k);

            int iSrc = data.indexMap.get(pipe.getSource());
            int iDst = data.indexMap.get(pipe.getTarget());

            double flow = flows[k]; // м³/с

            double areaSrc = data.reservoirs.get(iSrc).getArea();
            double areaDst = data.reservoirs.get(iDst).getArea();

            // dP[iSrc] -= ...
            dP[iSrc] -= flow * RHO * G / areaSrc;
            dP[iDst] += flow * RHO * G / areaDst;

            // Перенос воды
            double dVolume = flow * DT;
            h[iSrc] -= dVolume / areaSrc;
            h[iDst] += dVolume / areaDst;

            // Ограничения [0..MAX_LEVEL]
            if (h[iSrc] < 0) h[iSrc] = 0;
            if (h[iDst] > MAX_LEVEL) h[iDst] = MAX_LEVEL;
        }

        // 3.4) Применяем dP к P
        for (int i = 0; i < P.length; i++) {
            P[i] += dP[i] * DT;
            if (P[i] < 0) P[i] = 0;
        }

        // При желании можем печатать состояние на каждом (!) подшаге:
        // printAndPublish(P, h, data.reservoirs);
    }

    // ------------------------------------------------
    // 4) РАСЧЁТ РАСХОДОВ (газ + гидростатика)
    // ------------------------------------------------
    private double[] calculateFlows(SimulationData data, double[] P, double[] h) {
        double[] flows = new double[data.pipes.size()];

        for (int k = 0; k < data.pipes.size(); k++) {
            Pipe pipe = data.pipes.get(k);

            int iSrc = data.indexMap.get(pipe.getSource());
            int iDst = data.indexMap.get(pipe.getTarget());

            // Общее давление
            double totalSrc = P[iSrc] + RHO * G * h[iSrc];
            double totalDst = P[iDst] + RHO * G * h[iDst];

            double dp = totalSrc - totalDst; // Па

            double flow = 0.0;
            if (Math.abs(dp) > 1e-9) {
                double diam = pipe.getDiameter();
                double crossSection = Math.PI * Math.pow(diam / 2.0, 2);

                flow = Math.signum(dp)
                        * crossSection
                        * Math.sqrt(2.0 * Math.abs(dp) / RHO);
            }
            flows[k] = flow;
        }

        return flows;
    }

    // ------------------------------------------------
    // 5) СОХРАНЕНИЕ РЕЗУЛЬТАТОВ
    // ------------------------------------------------
    private void saveResults(List<Reservoir> reservoirs, double[] P, double[] h) {
        val reservoirRepo = repoResolver.resolve(Reservoir.class);

        for (int i = 0; i < reservoirs.size(); i++) {
            double newPressKPa = P[i] / 1000.0;
            double newLevel = h[i];

            Reservoir old = reservoirs.get(i);
            Reservoir upd = old.toBuilder()
                    .setPressure(newPressKPa) // кПа
                    .setLevel(newLevel)       // м
                    .build();
            reservoirs.set(i, upd);
        }

        // Можно сохранить по одному, либо saveAll:
        reservoirs.forEach(reservoirRepo::save);
    }

    // ------------------------------------------------
    // 6) ВЫВОД/ПУБЛИКАЦИЯ
    // ------------------------------------------------
    private List<ReservoirDto> printAndPublish(
            double[] P,
            double[] h,
            List<Reservoir> reservoirs
    ) {
        List<ReservoirDto> dtos = new ArrayList<>();
        // При желании, вывести консольный заголовок
        // System.out.println("=== Current state ===");

        for (int i = 0; i < P.length; i++) {
            double gas_kPa = P[i] / 1000.0;
            double lvl = Math.max(h[i], 0.0);
            double area = reservoirs.get(i).getArea();

            // Формируем DTO
            Long reservoirId = reservoirs.get(i).getId();
            Long spaceId = reservoirs.get(i).getSpace().getId();

            ReservoirDto dto = ReservoirDto.init()
                    .setId(reservoirId)
                    .setSpaceId(spaceId)
                    .setPressure(gas_kPa)
                    .setLevel(lvl)
                    .setArea(area)
                    .build();

            // Публикуем
            reservoirPublisher.publishUpdate(dto);

            dtos.add(dto);
        }

        // Если хотите консольный вывод - можно здесь выводить
        // System.out.println(dtos);

        return dtos;
    }

    // ------------------------------------------------
    // 7) ВСПОМОГАТЕЛЬНАЯ "МОДЕЛЬ" ДАННЫХ
    // ------------------------------------------------
    private static class SimulationData {
        final List<Reservoir> reservoirs;
        final List<Pipe> pipes;
        final Map<Reservoir, Integer> indexMap;

        SimulationData(List<Reservoir> r, List<Pipe> p) {
            this.reservoirs = r;
            this.pipes = p;

            Map<Reservoir, Integer> indexMap = new HashMap<>();
            for (int i = 0; i < reservoirs.size(); i++) {
                indexMap.put(reservoirs.get(i), i);
            }

            this.indexMap = indexMap;
        }
    }
}
