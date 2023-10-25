package eu.matherion.core.shared.commons;

import java.util.concurrent.TimeUnit;

public class Cooldown {

    private final long cooldown;
    private final TimeUnit timeUnit;
    private final long executionTime;

    public Cooldown(long cooldown, TimeUnit timeUnit) {
        this.cooldown = cooldown;
        this.timeUnit = timeUnit;
        executionTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - executionTime >= timeUnit.toMillis(cooldown);
    }

    public static Cooldown of(long cooldown, TimeUnit timeUnit) {
        return new Cooldown(cooldown, timeUnit);
    }
}
