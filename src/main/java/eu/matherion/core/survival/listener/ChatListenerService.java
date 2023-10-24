package eu.matherion.core.survival.listener;

import cz.maku.mommons.ExceptionResponse;
import cz.maku.mommons.worker.annotation.BukkitEvent;
import cz.maku.mommons.worker.annotation.Service;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.player.MatherionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Optional;

import static eu.matherion.core.survival.SurvivalConfiguration.BLOCKED_CHAT_MESSAGES;
import static eu.matherion.core.survival.SurvivalConfiguration.CHAT_TAGS;

@Service(listener = true)
public class ChatListenerService {

    private final CoreApplication coreApplication = CoreApplication.getPlugin(CoreApplication.class);
    private final List<String> bannedWords = coreApplication.getConfig().getStringList("banned-words");

    @BukkitEvent(AsyncPlayerChatEvent.class)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        for (String tag : CHAT_TAGS) {
            if (message.contains(tag)) {
                String replace = message.replace(tag, "§2" + tag + "§r");
                event.setMessage(replace);
                message = replace;
                Bukkit.getOnlinePlayers().forEach(possiblyTaggedPlayer -> {
                    if (possiblyTaggedPlayer.hasPermission("matherion.tag." + tag)) {
                        possiblyTaggedPlayer.playSound(possiblyTaggedPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                        possiblyTaggedPlayer.sendTitle("§c§lOznačení", "§cHráč §c§l" + player.getName() + "§c tě označil v chatu.", 1, 20 * 5, 20 * 2);
                    }
                });
            }
        }

        for (String blockedMessage : BLOCKED_CHAT_MESSAGES) {
            if (!message.equalsIgnoreCase(blockedMessage)) continue;
            event.setCancelled(true);
            player.sendMessage("§4§l! §cPřipojovací zpráva z telefonu zablokována!");
        }

        for (String bannedWord : bannedWords) {
            if (!message.toLowerCase().contains(bannedWord.toLowerCase())) continue;
            message = message.replace(bannedWord, "§c****§r");
            event.setMessage(message);
            player.sendMessage("§4§l! §cZpráva obsahuje zakázané slovo! Dávej si pozor!");
            Optional<MatherionPlayer> optionalMatherionPlayer = MatherionPlayer.of(player.getName());
            if (optionalMatherionPlayer.isPresent()) {
                MatherionPlayer matherionPlayer = optionalMatherionPlayer.get();
                matherionPlayer.getValueAsync("banned-words-warns").thenAccept(warns -> {
                    if (warns == null) warns = 0;
                    double warnsInt = (double) warns;
                    warnsInt++;
                    if (warnsInt == 3) {
                        Bukkit.getScheduler().runTask(CoreApplication.getPlugin(CoreApplication.class), () -> {
                            player.kickPlayer("§4§l! §cByl jsi vyhozen z důvodu použití zakázaných slov!");
                        });
                        warnsInt = 0;
                    }
                    matherionPlayer.setValueAsync("banned-words-warns", warnsInt, true).thenAccept(response -> {
                        System.out.println(response.getCode());
                        System.out.println(response.getContent());
                        if (response instanceof ExceptionResponse exceptionResponse) {
                            exceptionResponse.getException().printStackTrace();
                        }
                    });
                });
            }
        }

    }

}
