package github.navashield.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class MinehutEssentialsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("minehutessentials.json");

    private static final Map<String, FilterDefinition> DEFINITIONS = new LinkedHashMap<>();
    private static final Map<String, Boolean> STATES = new LinkedHashMap<>();

    static {
        register("adblock", "Ad block", "Blocks chat messages that contain [AD]", Target.CHAT,
                text -> text.contains("[AD]"));
        register("booptime", "Boop time",
                "Blocks player has spent X amount of time in boop arena",
                Target.CHAT, regex("(?s).*just reached \\d+ minutes in the Lobby \\d+ Boop Arena!.*"));
        register("boopwelcome", "Boop Arena welcome", "Blocks the Boop Arena welcome message",
                Target.CHAT, text -> text.contains("Welcome to the Minehut Boop Arena!"));
        register("lobbyjoin", "Lobby join notice", "Blocks announcing \"a player joined your lobby.\"",
                Target.CHAT, regex(".* joined your lobby\\.?"));
        register("minehutbanner", "Minehut banner messages",
                "Blocks all banner-style messages that include --------[MINEHUT]--------.",
                Target.CHAT, text -> text.contains("--------[MINEHUT]--------"));
        register("raidbossbar", "Raid countdown bossbar", "Removes the raid countdown bossbar",
                Target.BOSSBAR, containsAll("Minehut | Raid starts in", "Seconds"));
        register("raidready", "Raid ready", "Blocks the chat message that announces a new raid is ready",
                Target.CHAT, text -> text.contains("A new raid is ready! Enter the circle to begin."));
        register("rulesfilter", "Rules reminder", "Blocks rule reminder messages",
                Target.CHAT, text -> text.contains("Please follow the /rules in our lobbies. Thanks!"));
        register("vote", "Vote reward announcement", "Blocks vote reward announcements",
                Target.CHAT, regex("\\[Minehut\\] .* just got free credits and gems by voting via /vote"));
        resetToDefaults();
    }

    private MinehutEssentialsConfig() {
    }

    public static Collection<FilterDefinition> getDefinitions() {
        List<FilterDefinition> sorted = new ArrayList<>(DEFINITIONS.values());
        sorted.sort((left, right) -> left.key().compareTo(right.key()));
        return Collections.unmodifiableList(sorted);
    }

    public static boolean isEnabled(String key) {
        return STATES.getOrDefault(key, true);
    }

    public static void setEnabled(String key, boolean enabled) {
        if (!DEFINITIONS.containsKey(key)) {
            return;
        }

        STATES.put(key, enabled);
        saveConfig();
    }

    public static boolean shouldAllowChatOrSystem(String message) {
        return shouldAllow(message, Target.CHAT);
    }

    public static boolean shouldAllowBossbar(String message) {
        return shouldAllow(message, Target.BOSSBAR);
    }

    public static void loadConfig() {
        resetToDefaults();

        if (!Files.exists(CONFIG_PATH)) {
            saveConfig();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null && data.filters != null) {
                for (Map.Entry<String, Boolean> entry : data.filters.entrySet()) {
                    if (DEFINITIONS.containsKey(entry.getKey()) && entry.getValue() != null) {
                        STATES.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        } catch (IOException | JsonParseException ignored) {
            resetToDefaults();
        }

        saveConfig();
    }

    private static boolean shouldAllow(String message, Target target) {
        String normalized = Objects.toString(message, "");
        for (FilterDefinition definition : DEFINITIONS.values()) {
            if (definition.target() != target) {
                continue;
            }
            if (!isEnabled(definition.key())) {
                continue;
            }
            if (definition.matcher().test(normalized)) {
                return false;
            }
        }
        return true;
    }

    private static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                ConfigData data = new ConfigData();
                data.filters = new LinkedHashMap<>(STATES);
                GSON.toJson(data, writer);
            }
        } catch (IOException ignored) {
        }
    }

    private static void resetToDefaults() {
        STATES.clear();
        for (String key : DEFINITIONS.keySet()) {
            STATES.put(key, true);
        }
    }

    private static void register(String key, String label, String description, Target target, Predicate<String> matcher) {
        DEFINITIONS.put(key, new FilterDefinition(key, label, description, target, matcher));
    }

    private static Predicate<String> containsAll(String... parts) {
        List<String> values = new ArrayList<>(List.of(parts));
        return text -> values.stream().allMatch(text::contains);
    }

    private static Predicate<String> regex(String expression) {
        Pattern pattern = Pattern.compile(expression);
        return text -> pattern.matcher(text).matches();
    }

    public record FilterDefinition(String key, String label, String description, Target target, Predicate<String> matcher) {
    }

    public enum Target {
        CHAT,
        BOSSBAR
    }

    private static final class ConfigData {
        Map<String, Boolean> filters;
    }
}

