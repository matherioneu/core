package eu.matherion.core.survival.level;

import com.google.common.collect.Lists;
import cz.maku.mommons.bukkit.scheduler.Schedulers;
import cz.maku.mommons.cache.ExpiringMap;
import cz.maku.mommons.ef.Repositories;
import cz.maku.mommons.ef.repository.Repository;
import cz.maku.mommons.ef.statement.CompletedStatement;
import cz.maku.mommons.ef.statement.MySQLStatementImpl;
import cz.maku.mommons.ef.statement.StatementType;
import cz.maku.mommons.storage.database.type.MySQL;
import cz.maku.mommons.worker.annotation.*;
import eu.matherion.core.CoreApplication;
import eu.matherion.core.shared.placeholderapi.CorePlaceholders;
import eu.matherion.core.shared.player.MatherionPlayer;
import eu.matherion.core.shared.player.PlayerService;
import eu.matherion.core.survival.level.data.LevelData;
import eu.matherion.core.survival.level.event.MatherionLevelUpEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Service(scheduled = true)
public class LevelService {

    public static final ExpiringMap<String, LevelData> CACHE = ExpiringMap.from(2, ChronoUnit.MINUTES);
    private final CoreApplication coreApplication = CoreApplication.getPlugin(CoreApplication.class);
    @Getter
    private final List<Level> levels = Lists.newArrayList();
    private final Map<String, BiConsumer<Player, String>> rewardsTypes = Map.of(
            "[command]", (player, command) -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
    );
    @Getter
    private Repository<String, LevelData> repository;
    @Load
    private PlayerService playerService;

    @Initialize
    private void repository() {
        try {
            Connection connection = MySQL.getApi().getConnection();
            repository = Repositories.createRepository(connection, LevelData.class);
            //Tables.createSqlTable(connection, LevelData.class);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Initialize
    private void registerPlaceholders() {
        CorePlaceholders.register(new LevelPlaceholder());
    }

    @Initialize
    private void registerLevels() {
        ConfigurationSection section = coreApplication.getConfig().getConfigurationSection("levels");
        if (section == null) {
            coreApplication.getLogger().warning("Levels section not found!");
            return;
        }
        int maximumLevel = section.getInt("maximum");
        for (int i = 0; i < maximumLevel; i++) {
            int number = i + 1;
            int requiredExperience = (int) getRequiredExp(number);
            levels.add(new Level(number, requiredExperience, Lists.newArrayList(), Lists.newArrayList("§8+§b100 §7Kreditů", "", "§8§oKredity jsou prémiová měna, která lze", "§8§ouplatnit po celém serveru. Koupíš", "§8§opomocí ní třeba VIP, nebo s", "§8§oní můžeš nakupovat v hráčských", "§8§oobchodech...")));
        }
        /*for (String levelKey : section.getKeys(false)) {
            ConfigurationSection levelSection = section.getConfigurationSection(levelKey);
            if (levelSection == null) continue;
            int number = levelSection.getInt("number");
            int requiredExperience = levelSection.getInt("required-experience");
            List<String> rawRewards = levelSection.getStringList("rewards");
            List<BiConsumer<Player, LevelData>> rewards = Lists.newArrayList();
            for (String reward : rawRewards) {
                for (Map.Entry<String, BiConsumer<Player, String>> entry : rewardsTypes.entrySet()) {
                    if (reward.startsWith(entry.getKey())) {
                        String value = reward.replace(entry.getKey(), "");
                        rewards.add((player, levelData) -> entry.getValue().accept(player, value));
                        break;
                    }
                }
            }
            levels.add(new Level(number, requiredExperience, rewards));
            registeredLevels++;
        }
        coreApplication.getLogger().info("Registered " + registeredLevels + " levels!");*/
    }

    public int getRequiredExp(int level) {
        return 100 * level * level - 100 * (level - 1) * (level - 1);
    }

    public String createProgressBar(int nextLevel, int experience, int length, String completed, String need) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i < length * experience / getRequiredExp(nextLevel)) {
                stringBuilder.append(completed);
                continue;
            }
            stringBuilder.append(need);
        }
        return stringBuilder.toString();
    }

    public int getPercentage(int nextLevel, int experience) {
        return 100 * experience / getRequiredExp(nextLevel);
    }

    public String createProgressBar(int level, int experience) {
        return createProgressBar(level, experience, 30, "&6|", "&8|");
    }

    public LevelData getLevelData(String player) {
        LevelData levelData = repository.select(player);
        if (levelData == null) {
            levelData = new LevelData();
            levelData.setPlayer(player);
            levelData.setLevel(1);
            levelData.setExperience(0);
            levelData.setClaimedRewards(Lists.newArrayList());
            levelData.setColors(Lists.newArrayList(ChatColor.GRAY));
            levelData.setActiveColor(ChatColor.GRAY);
            try {
                repository.create(levelData);
            } catch (IllegalAccessException e) {
                coreApplication.getLogger().warning("Cannot create level data for player " + player + ": " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        return levelData;
    }

    public CompletableFuture<LevelData> getLevelDataAsync(String player) {
        return CompletableFuture.supplyAsync(() -> getLevelData(player));
    }

    public void cachePlayer(String player) {
        getLevelDataAsync(player).thenAccept(levelData -> CACHE.renew(player, levelData));
    }

    public CompletableFuture<Boolean> claimReward(String player, Level level) {
        return transactionData(player, levelData -> {
            List<Integer> claimedRewards = levelData.getClaimedRewards();
            if (claimedRewards.contains(level.getNumber())) return null;
            claimedRewards.add(level.getNumber());
            return levelData;
        });
    }

    public CompletableFuture<Boolean> unlockColor(String player, ChatColor color) {
        return transactionData(player, levelData -> {
            List<ChatColor> colors = levelData.getColors();
            if (colors.contains(color)) return null;
            colors.add(color);
            return levelData;
        });
    }

    public CompletableFuture<Boolean> transactionData(String player, Function<LevelData, LevelData> transaction) {
        return getLevelDataAsync(player).thenApplyAsync(levelData -> {
            LevelData newLevelData = transaction.apply(levelData);
            if (newLevelData == null) return false;
            try {
                repository.update(newLevelData);
                CACHE.renew(player, newLevelData);
                return true;
            } catch (IllegalAccessException e) {
                coreApplication.getLogger().warning("Cannot update level data for player " + player + ": " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> giveExperience(String player, int amount) {
        return transactionData(player, levelData -> {
            int experience = levelData.getExperience() + amount;
            Optional<MatherionPlayer> optionalMatherionPlayer = MatherionPlayer.of(player);
            optionalMatherionPlayer.ifPresent(matherionPlayer -> matherionPlayer.bukkit().sendMessage("§e+" + amount + " EXP"));
            int level = levelData.getLevel();
            int requiredExperience = getRequiredExp(level + 1);
            if (experience >= requiredExperience) {
                experience = 0;
                level++;
                int finalLevel = level;
                optionalMatherionPlayer.ifPresent(matherionPlayer -> {
                    Schedulers.later(task -> {
                        MatherionLevelUpEvent event = new MatherionLevelUpEvent(matherionPlayer, finalLevel);
                        Bukkit.getPluginManager().callEvent(event);
                    }, 30);
                });
            }
            levelData.setExperience(experience);
            levelData.setLevel(level);
            return levelData;
        });
    }

    @Repeat(delay = 25, period = 30 * 10)
    @Async
    private void synchronize() {
        List<String> players = playerService.getPlayers().values().stream().map(MatherionPlayer::getNickname).toList();
        if (players.isEmpty()) return;

        StringBuilder statement = new StringBuilder("SELECT * FROM core_levels WHERE ");
        for (int i = 0; i < players.size(); i++) {
            statement.append("player = '").append(players.get(i)).append("'");
            if (i != players.size() - 1) {
                statement.append(" OR ");
            }
        }
        MySQLStatementImpl mySQLStatement = new MySQLStatementImpl(statement.toString(), StatementType.SELECT);
        CompletedStatement<MySQLStatementImpl> completedStatement = mySQLStatement.complete(MySQL.getApi().getConnection());
        List<LevelData> levelsData = Lists.newArrayList(completedStatement.getRecords().stream().map(record -> {
            try {
                return repository.fromRecord(record);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }).toList());

        for (String player : players) {
            if (levelsData.stream().noneMatch(levelData -> levelData.getPlayer().equalsIgnoreCase(player))) {
                LevelData levelData = new LevelData();
                levelData.setPlayer(player);
                levelData.setLevel(1);
                levelData.setExperience(0);
                levelData.setClaimedRewards(Lists.newArrayList());
                levelData.setColors(Lists.newArrayList(ChatColor.GRAY));
                levelData.setActiveColor(ChatColor.GRAY);
                levelsData.add(levelData);
            }
        }

        for (LevelData data : levelsData) {
            CACHE.renew(data.getPlayer(), data);
        }
    }

}
