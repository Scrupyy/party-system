package de.scrupy.party.proxy;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.Scheduler;
import org.jetbrains.annotations.NotNull;

public class TaskExecutor {

    @NotNull
    private final ProxyServer proxyServer;
    @NotNull
    private final Object plugin;

    public TaskExecutor(@NotNull ProxyServer proxyServer, @NotNull Object plugin) {
        this.proxyServer = proxyServer;
        this.plugin = plugin;
    }

    public void execute(Runnable runnable) {
        Scheduler scheduler = proxyServer.getScheduler();
        scheduler.buildTask(plugin, runnable).schedule();
    }
}
