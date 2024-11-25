package eca;

import eca.config.ConfigurationService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Memory monitoring class. Used to clear memory if total memory used by application exceed specified threshold
 *
 * @author Roman Batygin
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoryMonitor {

    private static final ConfigurationService CONFIG_SERVICE = ConfigurationService.getApplicationConfigService();

    private static final int SECONDS_IN_MINUTES = 60;
    private static final int MILLIS_IN_SECOND = 1000;

    public static final MemoryMonitor INSTANCE = new MemoryMonitor();

    private final Timer timer = new Timer("MemoryMonitor");

    private boolean started;

    /**
     * Starts memory monitoring.
     */
    public synchronized void start() {
        if (started) {
            throw new IllegalStateException("Memory monitor is already started");
        }
        int memoryMonitoringPeriodMillis =
                CONFIG_SERVICE.getApplicationConfig().getMemoryMonitoringPeriodMinutes() * SECONDS_IN_MINUTES *
                        MILLIS_IN_SECOND;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Runtime.getRuntime().totalMemory() >
                        CONFIG_SERVICE.getApplicationConfig().getMemoryThresholdForGC()) {
                    System.gc();
                    log.info("Cleanup memory usage");
                }
            }
        }, memoryMonitoringPeriodMillis, memoryMonitoringPeriodMillis);
        log.info("Starting memory monitoring task");
        started = true;
    }
}
