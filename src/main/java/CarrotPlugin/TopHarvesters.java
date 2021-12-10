package CarrotPlugin;

import com.google.common.collect.Lists;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.text.NumberFormat;
import java.util.*;

public class TopHarvesters {

    Counter counter;
    World world;
    Plugin plugin;
    Integer firstPlaceCarrotCache = 0, secondPlaceCarrotCache = 0, thirdPlaceCarrotCache = 0;
    Component carrotcraft = Component.text("[", TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD).append
            (Component.text("CarrotCraft", TextColor.fromHexString("#FFAA00"))).append
            (Component.text("] ", TextColor.fromHexString("#FFFF55")));

    Component harvestedMsg = Component.text("Carrots Harvested: ", TextColor.fromHexString("#FFAA00"));

    private LinkedHashMap<String, Integer> sortByValue() {
        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();

        counter.carrotCounts.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        return reverseSortedMap;
    }

    public TopHarvesters(Counter initCounter, World _world, Plugin carrotplugin) {
        counter = initCounter;
        world = _world;
        plugin = carrotplugin;

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                    public void run() {
                        spawn();
                    }
                }
                , 100, 60);
    }


    public void spawn() {
        Collection<NPC> npcs = Lists.newArrayList(CitizensAPI.getNPCRegistry());
        NPC first = null, second = null, third = null;
        for (NPC npc : npcs) {
            String place = npc.data().get("place");
            if (place != null) {
                if (place.equalsIgnoreCase("first")) {
                    first = npc;
                } else if (place.equalsIgnoreCase("second")) {
                    second = npc;
                } else if (place.equalsIgnoreCase("third")) {
                    third = npc;
                }
            }
        }

        Integer score = 1;
        Map<String, Integer> sorted = sortByValue();
        for (Map.Entry<String, Integer> harvester : sorted.entrySet()) {
            NPC current = null;
            Integer carrotCache = null;
            if (score == 1) {
                current = first;
                carrotCache = firstPlaceCarrotCache;
            } else if (score == 2) {
                current = second;
                carrotCache = secondPlaceCarrotCache;
            } else if (score == 3) {
                current = third;
                carrotCache = thirdPlaceCarrotCache;
            }

            if (current == null) {
                score++;
                if (score > 3) break;
                continue;
            }

            Integer carrotsHarvested = harvester.getValue();

            if (current.getName().equals(harvester.getKey())) {
                if (carrotCache != carrotsHarvested) {
                    updateHarvestedDisplay(current, harvestedMsg.append(Component.text(NumberFormat.getNumberInstance(Locale.US).format(carrotsHarvested), TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD)));
                    if (score == 1) {
                        firstPlaceCarrotCache = carrotsHarvested;
                    } else if (score == 2) {
                        secondPlaceCarrotCache = carrotsHarvested;
                    } else if (score == 3) {
                        thirdPlaceCarrotCache = carrotsHarvested;
                    }
                }

                score++;
                if (score > 3) break;
                continue;
            } else {
                current.setName(harvester.getKey());
                updateHarvestedDisplay(current, harvestedMsg.append(Component.text(NumberFormat.getNumberInstance(Locale.US).format(carrotsHarvested), TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD)));
                if (score == 1) {
                    firstPlaceCarrotCache = carrotsHarvested;
                } else if (score == 2) {
                    secondPlaceCarrotCache = carrotsHarvested;
                } else if (score == 3) {
                    thirdPlaceCarrotCache = carrotsHarvested;
                }
            }

            score++;
            if (score > 3) break;
        }
    }

    public void updateHarvestedDisplay(NPC current, Component message) {
        if (current == null || message == null) return;
        Location armorStandLocation = new Location(world, current.getStoredLocation().getX(), current.getStoredLocation().getY() + .1, current.getStoredLocation().getZ());
        Collection<Entity> armorStands = world.getNearbyEntitiesByType(EntityType.ARMOR_STAND.getEntityClass(), armorStandLocation, .1);

        if (armorStands.size() > 0) {
            if (armorStands.size() > 1) {
                Iterator<Entity> asIterator = armorStands.iterator();
                while (asIterator.hasNext()) {
                    Entity as = asIterator.next();
                    if (as.getType() == EntityType.ARMOR_STAND) {
                        System.out.println(armorStands.size() + " Removing dupe armorstand!");
                        as.remove();
                    }
                }
                ArmorStand as = (ArmorStand) world.spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
                as.setGravity(false);
                as.setCanPickupItems(false);
                as.customName(message);
                as.setCustomNameVisible(true);
                as.setVisible(false);
                System.out.println("Spawned armor stand after dupe clearing!");
            } else {
                Entity armorStand = armorStands.iterator().next();
                if (armorStand.getType() == EntityType.ARMOR_STAND) {
                    armorStand.customName(message);
                }
            }

        } else {
            System.out.println("No armorstand found!");
            ArmorStand as = (ArmorStand) world.spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
            as.setGravity(false);
            as.setCanPickupItems(false);
            as.customName(message);
            as.setCustomNameVisible(true);
            as.setVisible(false);
            System.out.println("Spawned armorstand!!");
        }
    }


}
