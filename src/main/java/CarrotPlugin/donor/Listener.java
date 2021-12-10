package CarrotPlugin.donor;

import CarrotPlugin.DB.DB;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class Listener implements org.bukkit.event.Listener {

    Scoreboard scoreboard;
    Plugin plugin;
    NamespacedKey persistentKey;
    ArrayList<Projectile> arrows = new ArrayList<Projectile>();
    GUI gui;
    Map<String, UserState> donors;
    Permission perms;
    Set<String> donorNames;
    DB DB;
    HashMap<Projectile, ActiveArrowEffect> activeArrows = new HashMap<>();

    public Listener(Scoreboard scoreboard, Plugin plugin, GUI gui,
                    Map<String, UserState> donors, Permission perms,
                    Set<String> donorNames, DB DB) {
        this.scoreboard = scoreboard;
        this.plugin = plugin;
        this.gui = gui;
        this.donors = donors;
        this.perms = perms;
        this.donorNames = donorNames;
        this.DB = DB;
        persistentKey = new NamespacedKey(plugin, "spawnedItem");
        addParticleEffect();
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        if (donorNames.contains(name) && perms.has(p, "carrotcraft.donor")) DB.loadDonor(p, name);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();
        UserState state = donors.get(name);
        if (state != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    DB.saveDonorState(name, state);
                }
            });

            donors.remove(name);
        }
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player p) {
            String name = p.getName();
            if (donors.containsKey(name)) {
                UserState state = donors.get(name);
                if (state == null || state.arrowEffect == null || !state.arrowEffectsEnabled) return;
                activeArrows.put((Projectile) e.getProjectile(), new ActiveArrowEffect(state.arrowEffect, 1));
            }
        }
    }

    public void addParticleEffect() {
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {

                Iterator<Map.Entry<Projectile, ActiveArrowEffect>> iter = activeArrows.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<Projectile, ActiveArrowEffect> arrow = iter.next();
                    ActiveArrowEffect effect = arrow.getValue();

                    Projectile activeArrow = arrow.getKey();
                    if (!activeArrow.isValid()) {
                        iter.remove();
                        continue;
                    }

                    if (effect.ticks < effect.effect.spawnDelay) {
                        effect.ticks++;
                        continue;
                    }
                    effect.ticks = 1;
                    Location loc = activeArrow.getLocation();
                    if (effect.effect.effect == Particle.ITEM_CRACK) {
                        Item dropped = activeArrow.getWorld().dropItem(loc, new ItemStack(effect.effect.trailMaterial));
                        dropped.setCanPlayerPickup(false);
                        dropped.setCanMobPickup(false);
                        dropped.getPersistentDataContainer().set(persistentKey, PersistentDataType.BYTE, (byte) 0);
                        Bukkit.getScheduler().runTaskLater(plugin, dropped::remove, effect.effect.removeDelay);
                    } else {
                        loc.getWorld().spawnParticle(effect.effect.effect, loc, effect.effect.count, .25, 0.01, .25, effect.effect.speed, null);
                    }
                }
            }
        }, 0, 1);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            activeArrows.remove(e.getEntity());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        if (e.getWhoClicked() instanceof Player p) {
            String name = p.getName();
            if (donors.containsKey(name)) {
                UserState state = donors.get(name);
                if (state == null || state.active == null || state.activeType == 0) return;
                if (state.active == inv) {
                    e.setCancelled(true);
                    ItemStack clickedItem = e.getCurrentItem();
                    if (clickedItem == null || clickedItem.getType().isAir()) return;
                    gui.processClick(e.getRawSlot(), state, p);
                }
            }
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryDragEvent e) {
        Inventory inv = e.getInventory();
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            String name = p.getName();
            if (donors.containsKey(name)) {
                UserState state = donors.get(name);
                if (state == null || state.active == null) return;
                if (state.active == inv) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler()
    public void onInventoryPickupItemEvent(InventoryPickupItemEvent event) {
        if (event.getInventory().getType() == InventoryType.HOPPER) {
            if (event.getItem().getPersistentDataContainer().has(persistentKey, PersistentDataType.BYTE))
                event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (!perms.has(p, "carrotcraft.donor")) return;
        String name = p.getName();
        if (donors.containsKey(name)) {
            UserState state = donors.get(name);
            if (state == null || state.trailEffect == null || !state.trailEffectsEnabled) return;
            if (state.lastSpawn > System.currentTimeMillis() - state.trailEffect.spawnDelay) return;
            Location currentLoc = p.getLocation();
            if (state.lastLoc == null) state.lastLoc = currentLoc;
            if (state.lastLoc.getX() != currentLoc.getX() || state.lastLoc.getY() != currentLoc.getY() || state.lastLoc.getZ() != currentLoc.getZ()) {
                state.lastLoc = currentLoc;
                if (state.trailEffect.trail == Particle.ITEM_CRACK) {
                    Item dropped = p.getWorld().dropItem(currentLoc, new ItemStack(state.trailEffect.trailMaterial));
                    dropped.setCanPlayerPickup(false);
                    dropped.setCanMobPickup(false);
                    dropped.getPersistentDataContainer().set(persistentKey, PersistentDataType.BYTE, (byte) 0);
                    Bukkit.getScheduler().runTaskLater(plugin, dropped::remove, state.trailEffect.removeDelay);
                } else {
                    currentLoc.setY(currentLoc.getY() + .1);
                    p.getWorld().spawnParticle(state.trailEffect.trail, currentLoc, state.trailEffect.count, .25, 0.1, .25, state.trailEffect.speed, null);
                }
                state.lastSpawn = System.currentTimeMillis();
            }

        }
    }
}
