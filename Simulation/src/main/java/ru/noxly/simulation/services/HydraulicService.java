package ru.noxly.simulation.services;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import ru.noxly.simulation.models.entities.*;

import java.util.*;

@Service
public class HydraulicService {
    private static final double G = 9.81;
    private static final double RHO = 1000;
    private static final double DT = 0.5;
    private static final int ITERATIONS = 3;

    private final List<Reservoir> reservoirs = new ArrayList<>();
    private final List<Pipe> pipes = new ArrayList<>();

    public void initMockData() {
        // Инициализация резервуаров
        double[] areas = {1.0, 1.5, 2.0, 0.8, 1.2};
        double[] pressures = {
                RHO*G*5.0,
                RHO*G*3.0,
                RHO*G*2.0,
                RHO*G*4.0,
                RHO*G*1.0
        };

        for (int i = 0; i < 5; i++) {
            Reservoir r = Reservoir.init()
                    .setArea(areas[i])
                    .setPressure(pressures[i])
                    .setLevel(pressures[i] / (RHO * G))
                    .build();
            reservoirs.add(r);
        }

        // Инициализация труб
        int[][] conns = {{0,1}, {1,2}, {2,3}, {3,4}, {0,3}, {1,4}};
        double[] pipe_areas = {0.1, 0.2, 0.15, 0.05, 0.12, 0.18};

        for (int i = 0; i < conns.length; i++) {
            Pipe p = Pipe.init()
                    .setSource(reservoirs.get(conns[i][0]))
                    .setTarget(reservoirs.get(conns[i][1]))
                    .setDiameter(2 * Math.sqrt(pipe_areas[i] / Math.PI))
                    .build();
            pipes.add(p);
        }
    }

    @PostConstruct
    public void simulate() {
        initMockData();

        System.out.println("=== НАЧАЛЬНОЕ СОСТОЯНИЕ ===");
        printResults();

        for (int iter = 0; iter < ITERATIONS; iter++) {
            System.out.printf("\n=== ИТЕРАЦИЯ %d ===\n", iter+1);

            Map<Pipe, Double> flows = calculateFlows();
            printFlows(flows);

            updatePressures(flows);

            System.out.println("\nПосле обновления:");
            printResults();
        }
    }

    private Map<Pipe, Double> calculateFlows() {
        Map<Pipe, Double> flows = new HashMap<>();
        for (Pipe pipe : pipes) {
            double p1 = pipe.getSource().getPressure();
            double p2 = pipe.getTarget().getPressure();
            double dp = p1 - p2;
            double radius = pipe.getDiameter() / 2;
            double s = Math.PI * radius * radius;

            double flow = 0;
            if (dp != 0) {
                flow = s * Math.signum(dp) * Math.sqrt(2 * Math.abs(dp)/RHO);
            }
            flows.put(pipe, flow);
        }
        return flows;
    }

    private void printFlows(Map<Pipe, Double> flows) {
        System.out.println("\nТекущие потоки:");
        System.out.printf("%-15s %-10s %-10s %-12s%n",
                "Труба", "Источник", "Приемник", "Расход (м³/с)");

        flows.forEach((pipe, flow) -> {
            int src = reservoirs.indexOf(pipe.getSource());
            int dest = reservoirs.indexOf(pipe.getTarget());
            System.out.printf("Труба %d->%d     %-10d %-10d %-12.4f%n",
                    src, dest, src, dest, flow);
        });
    }

    private void updatePressures(Map<Pipe, Double> flows) {
        // Создаем карту для отслеживания новых версий резервуаров
        Map<Reservoir, Reservoir> updatedReservoirs = new HashMap<>();

        // Рассчитываем изменения давления
        Map<Reservoir, Double> pressureDeltas = new HashMap<>();
        reservoirs.forEach(r -> pressureDeltas.put(r, 0.0));

        flows.forEach((pipe, flow) -> {
            Reservoir src = pipe.getSource();
            Reservoir dest = pipe.getTarget();

            double delta = flow * DT * RHO * G;
            pressureDeltas.put(src, pressureDeltas.get(src) - delta / src.getArea());
            pressureDeltas.put(dest, pressureDeltas.get(dest) + delta / dest.getArea());
        });

        // Создаем новые объекты резервуаров
        pressureDeltas.forEach((oldRes, delta) -> {
            double newPressure = oldRes.getPressure() + delta;
            Reservoir newRes = oldRes.toBuilder()
                    .setPressure(newPressure)
                    .setLevel(newPressure / (RHO * G))
                    .build();
            updatedReservoirs.put(oldRes, newRes);
        });

        // Обновляем список резервуаров
        reservoirs.replaceAll(r -> updatedReservoirs.getOrDefault(r, r));

        // Обновляем связи в трубах
        pipes.replaceAll(pipe -> {
            Reservoir newSource = updatedReservoirs.getOrDefault(pipe.getSource(), pipe.getSource());
            Reservoir newTarget = updatedReservoirs.getOrDefault(pipe.getTarget(), pipe.getTarget());
            return pipe.toBuilder()
                    .setSource(newSource)
                    .setTarget(newTarget)
                    .build();
        });
    }

    public void printResults() {
        System.out.println("\nИтоговые параметры системы:");
        System.out.printf("%-10s %-15s %-12s %-12s%n",
                "Резервуар", "Давление (кПа)", "Уровень (м)", "Объём (м³)");

        for (Reservoir r : reservoirs) {
            System.out.printf("%-10d %-15.2f %-12.2f %-12.2f%n",
                    reservoirs.indexOf(r),
                    r.getPressure() / 1000,
                    r.getLevel(),
                    r.getArea() * r.getLevel());
        }
    }
}