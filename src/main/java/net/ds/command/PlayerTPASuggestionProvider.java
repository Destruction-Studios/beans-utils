package net.ds.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.ds.BeansUtils;
import net.ds.tpa.TPAManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerTPASuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        MinecraftServer server = Objects.requireNonNull(context.getSource().getServer());
        ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());
        String input = builder.getInput().toLowerCase();
        List<UUID> tpaList = TPAManager.listPlayerTPA(player.getUuid());
        List<UUID> tpaHereList = TPAManager.listPlayerTPAHere(player.getUuid());;

        if (tpaList != null) {
            for (UUID uuid : tpaList) {
                String name = Objects.requireNonNull(server.getPlayerManager().getPlayer(uuid)).getName().getString();

                builder.suggest(name);
            }
        }

        if (tpaHereList != null) {
            for (UUID uuid : tpaHereList) {
                String name = Objects.requireNonNull(server.getPlayerManager().getPlayer(uuid)).getName().getString();

                builder.suggest(name);
            }
        }

        return builder.buildFuture();
    }
}
