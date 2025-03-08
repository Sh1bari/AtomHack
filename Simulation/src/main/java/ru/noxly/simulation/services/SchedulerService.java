package ru.noxly.simulation.services;

import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.noxly.simulation.models.entities.Scheduler;
import ru.noxly.simulation.models.enums.SchedulerEnum;
import ru.noxly.simulation.models.enums.SchedulerStatus;
import ru.noxly.simulation.repositories.RepoResolver;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final RepoResolver resolver;
    private final TaskScheduler taskScheduler;
    private final ApplicationContext applicationContext;
    private final PressureIntegrationService pressureIntegrationService;

    private final Map<SchedulerEnum, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    // Храним хэши задач для проверки изменений
    private final Map<SchedulerEnum, Integer> schedulerHashes = new ConcurrentHashMap<>();

    /**
     * Проверка и перепланирование задач каждые 60 секунд
     */
    @Scheduled(fixedDelay = 10000)
    public void checkAndScheduleSchedulers() {
        List<Scheduler> schedulers = resolver.getSchedulerRepository().findByEnabledTrue();

        for (Scheduler scheduler : schedulers) {
            int currentHash = generateSchedulerHash(scheduler);

            // Проверка, изменился ли планировщик
            if (!Objects.equals(schedulerHashes.get(scheduler.getName()), currentHash)) {
                log.info("Обнаружены изменения в планировщике: " + scheduler.getName());

                // Остановка текущей задачи, если она существует
                cancelTask(scheduler.getName());

                // Планируем заново
                scheduleTask(scheduler);

                // Сохраняем новый хэш
                schedulerHashes.put(scheduler.getName(), currentHash);
            }
        }

        // Останавливаем задачи, которые больше не активны
        Set<SchedulerEnum> activeSchedulerNames = new HashSet<>();
        schedulers.forEach(s -> activeSchedulerNames.add(s.getName()));
        scheduledTasks.keySet().removeIf(name -> {
            if (!activeSchedulerNames.contains(name)) {
                cancelTask(name);
                schedulerHashes.remove(name);
                return true;
            }
            return false;
        });
    }

    /**
     * Генерация хэша для проверки изменений
     */
    private int generateSchedulerHash(Scheduler scheduler) {
        return Objects.hash(
                scheduler.getCronExpression(),
                scheduler.getFixedDelay(),
                scheduler.getStartTime(),
                scheduler.getType(),
                scheduler.getEnabled()
        );
    }

    /**
     * Отмена задачи
     */
    private void cancelTask(SchedulerEnum schedulerName) {
        ScheduledFuture<?> future = scheduledTasks.get(schedulerName);
        if (future != null) {
            future.cancel(false);
            scheduledTasks.remove(schedulerName);
            log.info("Отменена задача: " + schedulerName);
        }
    }

    /**
     * Планирование задачи
     */
    private void scheduleTask(Scheduler scheduler) {
        Runnable task = () -> {
            // Получаем прокси и вызываем executeTask через него
            SchedulerService proxy = applicationContext.getBean(SchedulerService.class);
            proxy.executeTask(scheduler.getName());
        };

        switch (scheduler.getType()) {
            case CRON -> {
                if (scheduler.getCronExpression() != null) {
                    ScheduledFuture<?> future = taskScheduler.schedule(task, new CronTrigger(scheduler.getCronExpression()));
                    scheduledTasks.put(scheduler.getName(), future);
                    log.info("Запланирована cron-задача: " + scheduler.getName());
                } else {
                    log.info("Не указано cron-выражение для планировщика: " + scheduler.getName());
                }
            }
            case FIXED_DELAY -> {
                if (scheduler.getFixedDelay() != null) {
                    ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(task, Duration.ofMillis(scheduler.getFixedDelay()));
                    scheduledTasks.put(scheduler.getName(), future);
                    log.info("Запланирована периодическая задача: " + scheduler.getName());
                } else {
                    log.info("Не указана фиксированная задержка для планировщика: " + scheduler.getName());
                }
            }
            case ONE_TIME -> {
                if (scheduler.getStartTime() != null) {
                    Date startDate = Date.from(scheduler.getStartTime().toInstant());
                    ScheduledFuture<?> future = taskScheduler.schedule(task, startDate.toInstant());
                    scheduledTasks.put(scheduler.getName(), future);
                    log.info("Запланирована одноразовая задача: " + scheduler.getName());
                } else {
                    log.info("Не указано время старта для одноразовой задачи: " + scheduler.getName());
                }
            }
            default -> log.info("Неизвестный тип планировщика: " + scheduler.getName());
        }
    }

    /**
     * Выполнение задачи с проверкой блокировки
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeTask(SchedulerEnum schedulerName) {
        try {
            // Захват блокировки для предотвращения одновременного выполнения
            resolver.getSchedulerRepository().findByNameForUpdate(schedulerName).ifPresent(scheduler -> {
                if (scheduler.getStatus() == SchedulerStatus.RUNNING) {
                    log.info("Задача уже выполняется другим сервисом: " + scheduler.getName());
                    return;
                }

                HashSet<String> Aboba = new HashSet<>();
                // Устанавливаем статус RUNNING
                scheduler.setStatus(SchedulerStatus.RUNNING);
                resolver.getSchedulerRepository().save(scheduler);

                try {
                    log.info("Выполняется задача: " + scheduler.getName());
                    runSchedulerLogic(scheduler);

                    // Устанавливаем статус COMPLETED
                    scheduler.setStatus(SchedulerStatus.COMPLETED);
                    scheduler.setLastRunAt(OffsetDateTime.now());
                    resolver.getSchedulerRepository().save(scheduler);
                } catch (Exception e) {
                    scheduler.setStatus(SchedulerStatus.FAILED);
                    resolver.getSchedulerRepository().save(scheduler);
                    log.info("Ошибка при выполнении задачи " + scheduler.getName() + ": " + e.getMessage());
                }
            });
        } catch (PessimisticLockException e) {
            log.info("Не удалось захватить блокировку для задачи (таймаут): " + schedulerName);
            // Обработка: можно просто залогировать или предпринять другую логику (например, отложить задачу)
        } catch (Exception e) {
            log.info("Общая ошибка при выполнении задачи " + schedulerName + ": " + e.getMessage());
        }
    }

    /**
     * Основная логика выполнения задач
     */
    @Transactional
    public void runSchedulerLogic(Scheduler scheduler) {
        switch (scheduler.getName()) {
            case PRESSURE_VALUE_GETTER_POSTGRES -> runPressureValueUpdaterPostgres();
            case PRESSURE_VALUE_GETTER_REDIS -> runPressureValueUpdaterRedis();
            default -> log.info("Неизвестная задача: " + scheduler.getName());
        }
    }

    private void runPressureValueUpdaterPostgres() {
        pressureIntegrationService.postgresUpdater();
    }

    private void runPressureValueUpdaterRedis() {
        pressureIntegrationService.redisUpdater();
    }
}