package github.navashield.client;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class minehutessentialsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MinehutEssentialsConfig.loadConfig();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> configNode = literal("config")
                    .executes(context -> {
                        sendConfigPanel(context.getSource());
                        return 1;
                    });

            LiteralArgumentBuilder<FabricClientCommandSource> setNode = literal("set");
            for (MinehutEssentialsConfig.FilterDefinition definition : MinehutEssentialsConfig.getDefinitions()) {
                setNode.then(literal(definition.key())
                        .then(argument("enabled", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean enabled = BoolArgumentType.getBool(context, "enabled");
                                    MinehutEssentialsConfig.setEnabled(definition.key(), enabled);
                                    sendConfigPanel(context.getSource());
                                    return 1;
                                }))
                );
            }

            dispatcher.register(literal("mhessentials").then(configNode.then(setNode)));
        });

        ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, gameProfile, params, timestamp) ->
                !isOnMinehutServer() || MinehutEssentialsConfig.shouldAllowChatOrSystem(message.getString())
        );
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) ->
                !isOnMinehutServer() || MinehutEssentialsConfig.shouldAllowChatOrSystem(message.getString())
        );
    }

    private static void sendConfigPanel(FabricClientCommandSource source) {
        MutableText panel = Text.literal("MHEssentials Blocklist");
        for (MinehutEssentialsConfig.FilterDefinition definition : MinehutEssentialsConfig.getDefinitions()) {
            panel.append(Text.literal("\n")).append(buildFilterLine(definition));
        }
        source.sendFeedback(panel);
    }

    private static Text buildFilterLine(MinehutEssentialsConfig.FilterDefinition definition) {
        boolean enabled = MinehutEssentialsConfig.isEnabled(definition.key());
        boolean nextState = !enabled;

        MutableText base = Text.literal("- " + definition.label() + ": " + (enabled ? "ON" : "OFF"))
                .styled(style -> style
                        .withColor(enabled ? Formatting.GREEN : Formatting.RED)
                        .withHoverEvent(new HoverEvent.ShowText(Text.literal(definition.description()))));

        MutableText toggleButton = Text.literal(enabled ? " [Disable]" : " [Enable]")
                .styled(style -> style
                        .withColor(nextState ? Formatting.GREEN : Formatting.RED)
                        .withUnderline(true)
                        .withClickEvent(new ClickEvent.RunCommand(
                                "/mhessentials config set " + definition.key() + " " + nextState))
                        .withHoverEvent(new HoverEvent.ShowText(
                                Text.literal("Click to set " + definition.key() + " to " + nextState))));

        return base.append(toggleButton);
    }

    public static boolean isOnMinehutServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getNetworkHandler() == null) {
            return false;
        }

        ServerInfo server = client.getCurrentServerEntry();
        if (server == null || server.address == null || server.address.isBlank()) {
            return false;
        }

        String host = server.address.toLowerCase(Locale.ROOT).trim();
        int portIndex = host.indexOf(':');
        if (portIndex >= 0) {
            host = host.substring(0, portIndex);
        }

        return host.equals("minehut.gg")
                || host.equals("minehut.com")
                || host.endsWith(".minehut.gg")
                || host.endsWith(".minehut.com");
    }
}
