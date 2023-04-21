package us.mytheria.wolfdungeonsxl.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Represents a party of players
 */
public class Party {
    private final Set<UUID> members;
    private final UUID leader;

    /**
     * Creates a party with the starting leader
     *
     * @param leader The leader of the party
     * @return The party
     */
    public static Party of(Player leader) {
        return new Party(leader.getUniqueId());
    }

    /**
     * Creates a party with the starting leader
     *
     * @param leader The leader of the party
     */
    public Party(UUID leader) {
        this.leader = leader;
        this.members = new HashSet<>();
    }

    /**
     * @param newMember The player to add to the party
     * @return true if the player was added, false if the player was already in the party
     */
    public boolean add(Player newMember) {
        return members.add(newMember.getUniqueId());
    }

    /**
     * @param oldMember The player to remove from the party
     * @return true if the player was removed, false if the player was not in the party
     */
    public boolean remove(Player oldMember) {
        return members.remove(oldMember.getUniqueId());
    }

    /**
     * @return The leader of the party
     */
    public UUID getLeader() {
        return leader;
    }

    /**
     * @return The leader of the party as a player. Null if the player is offline
     */
    @Nullable
    public Player getLeaderPlayer() {
        return Bukkit.getPlayer(leader);
    }

    /**
     * @return The leader's name. Will use Bukkit#getOfflinePlayer(UUID)
     */
    @NotNull
    public String getOfflineLeaderName() {
        return Bukkit.getOfflinePlayer(leader).getName();
    }

    /**
     * @param player The player to check
     * @return true if the player is in the party, false if not
     */
    public boolean isMember(Player player) {
        return members.contains(player.getUniqueId());
    }

    /**
     * @param player The player to check
     * @return true if the player is the leader of the party, false if not
     */
    public boolean isLeader(Player player) {
        return player.getUniqueId().compareTo(leader) == 0;
    }

    /**
     * @return The members of the party (excluding the leader)
     */
    public Set<UUID> getMembers() {
        return members;
    }

    /**
     * @return The members of the party (excluding the leader) as players. Players that are offline will not be included
     */
    public Set<Player> getPlayerMembers() {
        Set<Player> players = new HashSet<>();
        forAllPlayerMembers(players::add);
        return players;
    }

    /**
     * @return The members of the party (including the leader). Players that are offline will not be included
     */
    public Set<Player> getAllPlayerMembers() {
        Set<Player> players = new HashSet<>();
        forAllPlayerMembers(players::add);
        return players;
    }

    /**
     * @param action The action to perform on each member
     */
    public void forEachMember(Consumer<UUID> action) {
        members.forEach(action);
    }

    /**
     * @param action The action to perform on each member, including the leader
     */
    public void forAllMembers(Consumer<UUID> action) {
        action.accept(leader);
        members.forEach(action);
    }

    /**
     * @param action The action to perform on each member
     */
    public void forEachPlayerMember(Consumer<Player> action) {
        forEachMember(name -> {
            Player player = Bukkit.getPlayer(name);
            if (player == null)
                return;
            action.accept(player);
        });
    }

    /**
     * @param action The action to perform on each member, including the leader
     */
    public void forAllPlayerMembers(Consumer<Player> action) {
        forAllMembers(name -> {
            Player player = Bukkit.getPlayer(name);
            if (player == null)
                return;
            action.accept(player);
        });
    }

    /**
     * @param uuid The uuid of the player to remove
     */
    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    /**
     * @param player The player to remove
     */
    public void removeMember(Player player) {
        members.remove(player.getUniqueId());
    }
}
