package me.rosillogames.eggwars.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import me.rosillogames.eggwars.EggWars;
import me.rosillogames.eggwars.arena.Arena;
import me.rosillogames.eggwars.enums.ArenaStatus;
import me.rosillogames.eggwars.language.TranslationUtils;
import me.rosillogames.eggwars.player.EwPlayer;

public class PlayerJoinListener implements Listener
{
    @EventHandler
    public void preLoginChecker(AsyncPlayerPreLoginEvent asyncplayerpreloginevent)
    {
        if (!EggWars.bungee.isEnabled())
        {
            return;
        }

        Arena arena = EggWars.bungee.getArena();

        if (arena.getStatus().equals(ArenaStatus.LOBBY) || arena.getStatus().equals(ArenaStatus.STARTING))
        {
            if (arena.isFull())
            {
                asyncplayerpreloginevent.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER, TranslationUtils.getMessage("gameplay.lobby.cant_join.full"));
            }
        }
        else if (!EggWars.config.canSpectJoin)
        {
            if (arena.getStatus().equals(ArenaStatus.SETTING) || !arena.isSetup())
            {
                return;
            }

            asyncplayerpreloginevent.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER, TranslationUtils.getMessage("gameplay.lobby.cant_join.ingame"));
        }
    }

    @EventHandler
    public void register(final PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        if (EggWars.bungee.isEnabled() && EggWars.bungee.getArena().getStatus().equals(ArenaStatus.SETTING) && !player.hasPermission("eggwars.setup"))
        {
            player.kickPlayer(TranslationUtils.getMessage("commands.error.arena_in_edit_mode", player));
            return;
        }

        if (!EggWars.bungee.isEnabled() && EggWars.config.alwaysTpToLobby && EggWars.config.lobby != null)
        {
            player.teleport(EggWars.config.lobby);
        }

        EggWars.getDB().loadPlayer(player);
        final EwPlayer ewplayer = new EwPlayer(player);
        EggWars.players.add(ewplayer);

        if (player.getCustomName() == null)
        {
            player.setCustomName(player.getName());
        }

        if (EggWars.bungee.isEnabled())
        {
            Arena arena = EggWars.bungee.getArena();

            if (!arena.isSetup() || arena.getStatus().equals(ArenaStatus.SETTING))
            {
                if (arena.getLobby() != null)
                {
                    player.teleport(arena.getLobby());
                }
                else
                {
                    player.teleport(arena.getWorld().getSpawnLocation());
                }

                arena.sendToDo(player);
                return;
            }

            event.setJoinMessage(null);

            if (arena.getStatus().isLobby())
            {
                arena.joinArena(ewplayer, false, false);
            }
            else
            {
                arena.joinArena(ewplayer, true, true);
            }
        }
    }
}
