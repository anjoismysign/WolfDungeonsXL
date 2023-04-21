package us.mytheria.wolfdungeonsxl;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.message.BlobMessage;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.wolfdungeonsxl.director.WDManagerDirector;
import us.mytheria.wolfdungeonsxl.entities.Party;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class WolfDungeonsXL extends BlobPlugin {
    private WDManagerDirector director;
    private DungeonsAPI dungeonsAPI;

    private class PartyCmd implements CommandExecutor, TabCompleter {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
            if (args.length < 1) {
                sender.sendMessage("Usage: /dungeonparty <create|join|invite|leave|disband>");
                return true;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            String arg = args[0];
            switch (arg.toLowerCase()) {
                case "create" -> {
                    Party party = getManagerDirector().getDungeonManager()
                            .createParty(player);
                    if (party == null) {
                        BlobLibAssetAPI.getMessage("Party.Already-In-Party")
                                .handle(player);
                        return true;
                    }
                    BlobLibAssetAPI.getMessage("Party.Create")
                            .handle(player);
                    return true;
                }
                case "leave" -> {
                    Party party = getManagerDirector().getDungeonManager()
                            .getParty(player);
                    if (party == null) {
                        BlobLibAssetAPI.getMessage("Party.Not-In-Party")
                                .handle(player);
                        return true;
                    }
                    if (party.isLeader(player)) {
                        BlobLibAssetAPI.getMessage("Party.Leader-Leave")
                                .handle(player);
                        return true;
                    }
                    party.removeMember(player);
                    BlobLibAssetAPI.getMessage("Party.Leave")
                            .modder()
                            .replace("%player%", party.getOfflineLeaderName())
                            .get()
                            .handle(player);
                    BlobMessage message = BlobLibAssetAPI.getMessage("Party.Left")
                            .modder().replace("%player%", player.getName()).get();
                    party.forAllPlayerMembers(message::handle);
                    return true;
                }
                case "join" -> {
                    List<UUID> invites = director.getDungeonManager().getInvites(player);
                    if (invites == null) {
                        BlobLibAssetAPI.getMessage("Party.No-Invitations")
                                .handle(player);
                        return true;
                    }
                    if (args.length < 2) {
                        BlobLibAssetAPI.getMessage("Party.Join-Usage")
                                .handle(player);
                        return true;
                    }
                    String playerName = args[1];
                    Player target = Bukkit.getPlayer(playerName);
                    if (target == null) {
                        BlobLibAssetAPI.getMessage("Player.Not-Found")
                                .handle(player);
                        return true;
                    }
                    if (!invites.contains(target.getUniqueId())) {
                        BlobLibAssetAPI.getMessage("Party.Not-Invited")
                                .handle(player);
                        return true;
                    }
                    Party party = director.getDungeonManager().getParty(target);
                    if (party == null) {
                        BlobLibAssetAPI.getMessage("Party.Target-Not-In-Party")
                                .handle(player);
                        return true;
                    }
                    director.getDungeonManager().add(player, party);
                    BlobLibAssetAPI.getMessage("Party.Joined")
                            .modder()
                            .replace("%player%", party.getOfflineLeaderName())
                            .get()
                            .handle(player);
                    party.forAllPlayerMembers(p ->
                            BlobLibAssetAPI.getMessage("Party.Join")
                                    .modder()
                                    .replace("%player%", player.getName())
                                    .get()
                                    .handle(p));
                    return true;
                }
                case "invite" -> {
                    Party party = director.getDungeonManager().getParty(player);
                    if (party == null) {
                        BlobLibAssetAPI.getMessage("Party.Not-In-Party")
                                .handle(player);
                        return true;
                    }
                    if (!party.isLeader(player)) {
                        BlobLibAssetAPI.getMessage("Party.Not-Leader")
                                .handle(player);
                        return true;
                    }
                    if (args.length < 2) {
                        BlobLibAssetAPI.getMessage("Party.Invite-Usage")
                                .handle(player);
                        return true;
                    }
                    String playerName = args[1];
                    Player target = Bukkit.getPlayer(playerName);
                    if (target == null) {
                        BlobLibAssetAPI.getMessage("Player.Not-Found")
                                .handle(player);
                        return true;
                    }
                    if (party.isMember(target)) {
                        BlobLibAssetAPI.getMessage("Party.Already-In-Party-Target")
                                .handle(player);
                        return true;
                    }
                    director.getDungeonManager().invite(target, target);
                    BlobLibAssetAPI.getMessage("Party.Invited")
                            .modder()
                            .replace("%player%", player.getName())
                            .get()
                            .handle(target);
                    party.forAllPlayerMembers(p ->
                            BlobLibAssetAPI.getMessage("Party.Invite")
                                    .modder()
                                    .replace("%player%", target.getName())
                                    .get()
                                    .handle(p));
                    return true;
                }
                case "disband" -> {
                    Party party = director.getDungeonManager().getParty(player);
                    if (party == null) {
                        BlobLibAssetAPI.getMessage("Party.Not-In-Party")
                                .handle(player);
                        return true;
                    }
                    if (!party.isLeader(player)) {
                        BlobLibAssetAPI.getMessage("Party.Not-Leader")
                                .handle(player);
                        return true;
                    }
                    director.getDungeonManager().disband(party);
                    BlobLibAssetAPI.getMessage("Party.Disband")
                            .handle(player);
                    party.forAllPlayerMembers(p ->
                            BlobLibAssetAPI.getMessage("Party.Disbanded")
                                    .modder()
                                    .replace("%player%", player.getName())
                                    .get()
                                    .handle(p));
                    return true;
                }
                default -> {
                    BlobLibAssetAPI.getMessage("Party.Invalid-Argument")
                            .handle(player);
                    return true;
                }
            }
        }

        @Nullable
        @Override
        public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
            if (!command.getName().equalsIgnoreCase("dungeonparty"))
                return null;
            if (args.length == 1)
                return List.of("create", "join", "invite", "leave", "disband");
            if (args.length == 2) {
                String arg = args[0].toLowerCase();
                if (arg.equals("join") || arg.equals("invite"))
                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
            return null;
        }
    }

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("DungeonsXL")) {
            Bukkit.getLogger().severe("DungeonsXL not found! Disabling WolfDungeonsXL...");
            return;
        }
        dungeonsAPI = (DungeonsAPI) Bukkit.getPluginManager().getPlugin("DungeonsXL");
        director = new WDManagerDirector(this);
        PartyCmd partyCmd = new PartyCmd();
        PluginCommand command = getCommand("dungeonparty");
        command.setExecutor(partyCmd);
        command.setTabCompleter(partyCmd);
    }

    @Override
    public void onDisable() {
        director.unload();
    }

    @Override
    public WDManagerDirector getManagerDirector() {
        return director;
    }

    /**
     * Get the DungeonsAPI instance
     *
     * @return DungeonsAPI instance
     */
    public DungeonsAPI getDungeonsAPI() {
        return dungeonsAPI;
    }
}
