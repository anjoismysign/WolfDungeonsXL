package us.mytheria.wolfdungeonsxl.director;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import us.mytheria.wolfdungeonsxl.entities.Party;

import java.util.*;

public class DungeonManager extends WDManager {
    private final Map<UUID, List<UUID>> invites;
    private final Map<UUID, Party> parties;
    private final Map<String, Location> homes;

    public DungeonManager(WDManagerDirector managerDirector) {
        super(managerDirector);
        this.homes = new HashMap<>();
        this.parties = new HashMap<>();
        this.invites = new HashMap<>();
    }

    /**
     * Will add a player to the home list.
     *
     * @param player The player to add.
     */
    public void add(Player player) {
        homes.put(player.getName(), player.getLocation().clone());
    }

    /**
     * Will remove a player from the home list and teleport him to his home.
     *
     * @param player The player to remove.
     */
    public void remove(Player player) {
        if (!homes.containsKey(player.getName()))
            return;
        Location home = homes.get(player.getName());
        player.teleport(home);
        homes.remove(player.getName());
        parties.remove(player.getUniqueId());
    }

    /**
     * @param player The player to add.
     * @return True if player has a party.
     */
    public boolean hasParty(Player player) {
        return parties.containsKey(player.getUniqueId());
    }

    /**
     * @param player The player to get the party of.
     * @return The party of the player. Null if the player has no party.
     */
    @Nullable
    public Party getParty(Player player) {
        return parties.get(player.getUniqueId());
    }

    /**
     * @param player The player to add.
     * @return The party of the player.
     */
    public Party createParty(Player player) {
        if (hasParty(player))
            return null;
        Party party = Party.of(player);
        parties.put(player.getUniqueId(), party);
        return party;
    }

    /**
     * @param newMember The player to add.
     * @param party     The party to add the player to.
     * @return True if the player was added to the party.
     */
    public boolean add(Player newMember, Party party) {
        if (hasParty(newMember))
            return false;
        if (!party.add(newMember))
            return false;
        parties.put(newMember.getUniqueId(), party);
        return true;
    }

    /**
     * @param existentMember The player to remove.
     * @param party          The party to remove the player from.
     * @return True if the player was removed from the party.
     */
    public boolean remove(Player existentMember, Party party) {
        if (!hasParty(existentMember))
            return false;
        if (!party.remove(existentMember))
            return false;
        parties.remove(existentMember.getUniqueId());
        return true;
    }

    /**
     * Disbands a party.
     *
     * @param party The party to disband.
     */
    public void disband(Party party) {
        party.forAllPlayerMembers(this::remove);
    }

    /**
     * Will invite a player to a party.
     *
     * @param partyLeader   The player to invite.
     * @param invitedPlayer The player to invite.
     * @return True if the player was invited. False if the partyLeader has no party.
     */
    public boolean invite(Player partyLeader, Player invitedPlayer) {
        UUID partyLeaderUUID = partyLeader.getUniqueId();
        return Optional.ofNullable(parties.get(partyLeaderUUID))
                .map(party -> {
                    UUID invitedPlayerUUID = invitedPlayer.getUniqueId();
                    invites.computeIfAbsent(invitedPlayerUUID, uuid -> new ArrayList<>()).add(partyLeaderUUID);
                    return true;
                })
                .orElse(false);
    }

    @Nullable
    public List<UUID> getInvites(Player player) {
        return invites.get(player.getUniqueId());
    }
}
