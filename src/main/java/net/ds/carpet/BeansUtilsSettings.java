package net.ds.carpet;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class BeansUtilsSettings {
    public static final String MODNAME = "BeansUtils";

    private static class ServerSideOnlyRuleCondition implements Rule.Condition {
        @Override
        public boolean shouldRegister() {
            return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
        }
    }

    @Rule(
            options = {"0", "25", "50", "75", "100", "150", "200"},
            strict = false,
            categories = {MODNAME, RuleCategory.SURVIVAL}
    )
    public static int mobExpPercent = 100;
}
