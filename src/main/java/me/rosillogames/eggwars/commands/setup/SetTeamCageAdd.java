package me.rosillogames.eggwars.commands.setup;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rosillogames.eggwars.EggWars;
import me.rosillogames.eggwars.arena.Arena;
import me.rosillogames.eggwars.commands.CommandArg;
import me.rosillogames.eggwars.language.TranslationUtils;
import me.rosillogames.eggwars.utils.TeamTypes;
import me.rosillogames.eggwars.utils.TeamUtils;

public class SetTeamCageAdd extends CommandArg
{
    public SetTeamCageAdd()
    {
        super(true);
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args)
    {
        if (args.length != 2)
        {
            TranslationUtils.sendMessage("commands.addTeamCage.usage", commandSender);
            return false;
        }

        Player player = (Player)commandSender;
        Arena arena;

        if ((arena = Arena.checkEditArena(player)) == null)
        {
            return false;
        }

        TeamTypes teamtypes = TeamUtils.typeByIdAndValidateForArena(arena, args[1], commandSender);

        if (teamtypes == null)
        {
            return false;
        }

        arena.getTeams().get(teamtypes).addCage(player.getLocation());
        TranslationUtils.sendMessage("commands.addTeamCage.success", commandSender, teamtypes.id());
        arena.sendToDo(player);
        return true;
    }

    @Override
    public List<String> getCompleteArgs(CommandSender commandSender, String[] args)
    {
        List<String> list = new ArrayList();

        if (args.length == 2)
        {
            Arena arena = EggWars.getArenaManager().getArenaByWorld(((Player)commandSender).getWorld());

            if (arena != null)
            {
                for (TeamTypes teamtypes : arena.getTeams().keySet()) 
                {
                    if (teamtypes.id().startsWith(args[1]))
                    {
                        list.add(teamtypes.id());
                    }
                }
            }
        }

        return list;
    }
}
