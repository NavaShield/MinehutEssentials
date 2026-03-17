package github.navashield.mixin.client;

import github.navashield.client.MinehutEssentialsConfig;
import github.navashield.client.minehutessentialsClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Map;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    private Collection<ClientBossBar> minehutessentials$filterBars(Map<?, ClientBossBar> instance) {
        Collection<ClientBossBar> values = instance.values();
        if (!minehutessentialsClient.isOnMinehutServer()) {
            return values;
        }

        return values.stream()
                .filter(bar -> MinehutEssentialsConfig.shouldAllowBossbar(bar.getName().getString()))
                .toList();
    }
}

