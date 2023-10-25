package eu.matherion.core.shared.maintenance;

import cz.maku.mommons.discord.Webhook;
import cz.maku.mommons.server.Server;
import cz.maku.mommons.worker.annotation.Load;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.shared.SharedConfiguration;
import eu.matherion.matherionwhitelist.WhitelistService;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
@Getter
public class MaintenanceService {

    private boolean serverClosed;
    private String reason;

    @Load
    private WhitelistService whitelistService;

    public CompletableFuture<Boolean> closeServer(String reason, String author) {
        this.serverClosed = true;
        this.reason = reason;
        whitelistService.setEnabled(true);
        return sendWebhook(author, true, reason);
    }

    public CompletableFuture<Boolean> openServer(String reason, String author) {
        this.serverClosed = false;
        this.reason = reason;
        whitelistService.setEnabled(false);
        return sendWebhook(author, false, reason);
    }

    private CompletableFuture<Boolean> sendWebhook(String author, boolean close, String reason) {
        String webhookUrl = SharedConfiguration.webhook("maintenance");
        if (webhookUrl == null) return CompletableFuture.completedFuture(false);
        Webhook webhook = new Webhook(webhookUrl);
        webhook.setUsername(author);
        webhook.setAvatarUrl(String.format("https://cravatar.eu/helmavatar/%s/600.png", author));
        Webhook.EmbedObject embedObject = new Webhook.EmbedObject();
        embedObject.setTitle(Server.local().getId());
        if (close) {
            embedObject.setDescription(":warnign: Údržba");
            embedObject.setColor(Color.orange);
        } else {
            embedObject.setDescription(":white_check_mark: Online");
            embedObject.setColor(Color.green);
        }
        embedObject.addField("Důvod", reason, true);
        webhook.addEmbed(embedObject);
        return CompletableFuture.supplyAsync(() -> {
            try {
                webhook.execute();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

}
