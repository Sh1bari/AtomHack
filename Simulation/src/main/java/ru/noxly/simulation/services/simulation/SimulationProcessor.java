package ru.noxly.simulation.services.simulation;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.noxly.simulation.models.entities.Pipe;
import ru.noxly.simulation.models.entities.Reservoir;
import ru.noxly.simulation.models.entities.Space;
import ru.noxly.simulation.models.models.dtos.ReservoirDto;
import ru.noxly.simulation.models.models.socket.ReservoirUpdateSocketDto;
import ru.noxly.simulation.redis.ReservoirPublisher;
import ru.noxly.simulation.repositories.RepoResolver;
import ru.noxly.simulation.services.ReservoirRedisService;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static ru.noxly.simulation.models.models.socket.SocketEntityEnum.UPDATE;
import static ru.noxly.simulation.utils.CommonUtils.nullOrApply;
import static ru.noxly.simulation.utils.CommonUtils.nullOrDefault;
import static ru.noxly.simulation.utils.ReservoirIdUtil.resolveReservoirId;

@Service
@RequiredArgsConstructor
public class SimulationProcessor {

    private static final double G = 9.81;       // м/с²
    private static final double RHO = 1000;       // кг/м³
    private static final double DT = 0.1;        // шаг по времени (с)
    private static final int N_STEPS = 5;     // число итераций за один запуск
    private static final double MAX_LEVEL = 10.0;

    private final RepoResolver repoResolver;
    private final ReservoirPublisher reservoirPublisher;
    private final ReservoirRedisService reservoirRedisService;
    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void simulateForSpace(Space space) {
        SimulationData data = loadDataFromDB(space);
        if (isNull(data)) {
            return;
        }

        double[] P = new double[data.reservoirs.size()];
        double[] h = new double[data.reservoirs.size()];
        initPressuresAndLevels(data, P, h);

        for (int step = 0; step < N_STEPS; step++) {
            doOneStepOfSimulation(data, P, h);
            printAndPublish(P, h, data.reservoirs);
        }

        saveResults(data.reservoirs, P, h);
    }

    private SimulationData loadDataFromDB(final Space spaceStart) {

        val space = entityManager.merge(spaceStart);

        if (isNull(space.getReservoirs()) || space.getReservoirs().isEmpty()) {
            return null;
        }

        List<Reservoir> resList = space.getReservoirs().stream()
                .map(r -> r.toBuilder()
                        .setPressure(
                                nullOrDefault(
                                        nullOrApply(reservoirRedisService.findById(resolveReservoirId(r.getId())),
                                                ru.noxly.simulation.models.entities.redis.Reservoir::getPressure),
                                        r.getPressure()
                                ) * 1000.0
                        )
                        .build()
                )
                .toList();

        List<Reservoir> reservoirs = new ArrayList<>(resList);

        List<Pipe> pipes = reservoirs.stream()
                .map(Reservoir::getOutgoingPipes)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        return new SimulationData(reservoirs, pipes);
    }

    private void initPressuresAndLevels(SimulationData data, double[] P, double[] h) {
        for (int i = 0; i < data.reservoirs.size(); i++) {
            Reservoir r = data.reservoirs.get(i);
            P[i] = r.getPressure();
            double level = r.getLevel();
            if (level > MAX_LEVEL) level = MAX_LEVEL;
            h[i] = level;
        }
    }

    private void doOneStepOfSimulation(SimulationData data, double[] P, double[] h) {
        double[] flows = calculateFlows(data, P, h);

        double[] dP = new double[data.reservoirs.size()];
        Arrays.fill(dP, 0.0);

        for (int k = 0; k < data.pipes.size(); k++) {
            Pipe pipe = data.pipes.get(k);

            int iSrc = data.indexMap.get(pipe.getSource());
            int iDst = data.indexMap.get(pipe.getTarget());

            double flow = flows[k]; // м³/с

            double areaSrc = data.reservoirs.get(iSrc).getArea();
            double areaDst = data.reservoirs.get(iDst).getArea();

            dP[iSrc] -= flow * RHO * G / areaSrc;
            dP[iDst] += flow * RHO * G / areaDst;

            double dVolume = flow * DT;
            h[iSrc] -= dVolume / areaSrc;
            h[iDst] += dVolume / areaDst;

            if (h[iSrc] < 0) h[iSrc] = 0;
            if (h[iDst] > MAX_LEVEL) h[iDst] = MAX_LEVEL;
        }

        for (int i = 0; i < P.length; i++) {
            P[i] += dP[i] * DT;
            if (P[i] < 0) P[i] = 0;
        }

    }

    private double[] calculateFlows(SimulationData data, double[] P, double[] h) {
        double[] flows = new double[data.pipes.size()];

        for (int k = 0; k < data.pipes.size(); k++) {
            Pipe pipe = data.pipes.get(k);

            int iSrc = data.indexMap.get(pipe.getSource());
            int iDst = data.indexMap.get(pipe.getTarget());

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

        reservoirs.forEach(o -> {
            reservoirRedisService.save(ru.noxly.simulation.models.entities.redis.Reservoir.init()
                    .setId(resolveReservoirId(o.getId()))
                    .setPressure(o.getPressure())
                    .build());
            reservoirRepo.save(o);
        });
    }

    private List<ReservoirDto> printAndPublish(
            double[] P,
            double[] h,
            List<Reservoir> reservoirs
    ) {
        List<ReservoirDto> dtos = new ArrayList<>();

        for (int i = 0; i < P.length; i++) {
            double gas_kPa = P[i] / 1000.0;
            double lvl = Math.max(h[i], 0.0);
            double area = reservoirs.get(i).getArea();

            Long reservoirId = reservoirs.get(i).getId();
            Long spaceId = reservoirs.get(i).getSpace().getId();

            ReservoirDto dto = ReservoirDto.init()
                    .setId(reservoirId)
                    .setSpaceId(spaceId)
                    .setPressure(gas_kPa)
                    .setLevel(lvl)
                    .setArea(area)
                    .build();

            val socketModel = ReservoirUpdateSocketDto.init()
                    .setSpaceId(spaceId)
                    .setCommand(UPDATE)
                    .setReservoir(dto)
                    .build();

            reservoirPublisher.publishUpdate(socketModel);

            dtos.add(dto);
        }

        return dtos;
    }

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
