package CarrotPlugin.donor;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Map;

public class GUI {
    Scoreboard scoreboard;
    Map<String, UserState> donors;
    Permission perms;

    String[] teams = new String[]{"blue", "red", "white", "aqua", "black", "dark_aqua", "dark_blue", "dark_gray",
            "dark_green", "dark_purple", "dark_red", "gold", "gray", "green", "light_purple", "yellow"};

    Component carrotcraft = Component.text("[", TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD).append
            (Component.text("CarrotCraft", TextColor.fromHexString("#FFAA00"))).append
            (Component.text("] ", TextColor.fromHexString("#FFFF55")));

    public GUI(Scoreboard scoreboard, Map<String, UserState> donors, Permission perms) {
        this.donors = donors;
        this.perms = perms;
        registerTeams(scoreboard);
    }

    Component composeMsg(String effect, String color) {
        if (color == null) color = "#00f636";
        return TextComponent.ofChildren(carrotcraft,
                Component.text("Enabled ", TextColor.fromHexString("#00f636")),
                Component.text(effect, TextColor.fromHexString(color), TextDecoration.BOLD));
    }

    public Inventory openDonorGUI(String name) {
        UserState state = donors.get(name);
        if (state == null) return null;

        Inventory donorGUI = Bukkit.createInventory(null, 54, Component.text("CarrotCraft Donor GUI"));

        if (state.arrowEffectsEnabled) {
            donorGUI.setItem(19, createItemGUI(Material.SPECTRAL_ARROW,
                    Component.text("Arrow Effects", TextColor.fromHexString("#f6ec00"))
                            .decoration(TextDecoration.ITALIC, false)));
            if (state.arrowEffect != null) {
                donorGUI.setItem(28, createItemGUI(Material.BARRIER,
                        Component.text("Remove Active Effect", TextColor.fromHexString("#f60000"))
                                .decoration(TextDecoration.ITALIC, false)));
            }
        }


        if (state.glowEffectsEnabled) {
            donorGUI.setItem(22, createItemGUI(Material.LANTERN, Component.text("Glow Effects", TextColor.fromHexString("#0c8ae7"))
                    .decoration(TextDecoration.ITALIC, false)));

            if (state.glowing != null) {
                donorGUI.setItem(31, createItemGUI(Material.BARRIER,
                        Component.text("Remove Active Effect", TextColor.fromHexString("#f60000"))
                                .decoration(TextDecoration.ITALIC, false)));
            }
        }

        if (state.trailEffectsEnabled) {
            donorGUI.setItem(25, createItemGUI(Material.CARROT,
                    Component.text("Trail Effects", TextColor.fromHexString("#dda010"))
                            .decoration(TextDecoration.ITALIC, false)));
            if (state.trailEffect != null) {
                donorGUI.setItem(34, createItemGUI(Material.BARRIER,
                        Component.text("Remove Active Effect", TextColor.fromHexString("#f60000"))
                                .decoration(TextDecoration.ITALIC, false)));
            }
        }
        state.active = donorGUI;
        state.activeType = 1;
        return donorGUI;
    }

    public Inventory openArrowGUI(String name) {
        UserState state = donors.get(name);
        if (state == null) return null;
        if (!state.arrowEffectsEnabled) return null;

        Inventory arrowEffects = Bukkit.createInventory(null, 54, Component.text("Arrow Effects"));

        for (Map.Entry<Integer, ArrowEffect> entry : state.arrowEffects.entrySet()) {
            ArrowEffect effect = entry.getValue();
            if (effect.material != null) {
                arrowEffects.setItem(entry.getKey(), createItemGUI(effect.material,
                        Component.text(effect.effectName, TextColor.fromHexString(effect.nameColor))
                                .decoration(TextDecoration.ITALIC, false)));
            } else {
                arrowEffects.setItem(entry.getKey(), createArrowGUI(Material.TIPPED_ARROW,
                        Component.text(effect.effectName, TextColor.fromHexString(effect.nameColor))
                                .decoration(TextDecoration.ITALIC, false), effect.potionType));
            }

        }

        arrowEffects.setItem(49, createItemGUI(Material.BARRIER,
                Component.text("Back", TextColor.fromHexString("#f60000"))
                        .decoration(TextDecoration.ITALIC, false)));
        state.active = arrowEffects;
        state.activeType = 2;
        return arrowEffects;
    }

    public Inventory openGlowGUI(String name) {
        UserState state = donors.get(name);
        if (state == null) return null;

        Inventory glowEffects = Bukkit.createInventory(null, 54, Component.text("Glow Effects"));

        for (Map.Entry<Integer, GlowEffect> entry : state.glowEffects.entrySet()) {
            GlowEffect effect = entry.getValue();
            glowEffects.setItem(entry.getKey(), createItemGUI(effect.invItem,
                    Component.text(effect.effectName, TextColor.fromHexString(effect.nameColor))
                            .decoration(TextDecoration.ITALIC, false)));
        }

        glowEffects.setItem(49, createItemGUI(Material.BARRIER,
                Component.text("Back", TextColor.fromHexString("#f60000"))
                        .decoration(TextDecoration.ITALIC, false)));
        state.active = glowEffects;
        state.activeType = 3;
        return glowEffects;
    }

    public Inventory openTrailGUI(String name) {
        UserState state = donors.get(name);
        if (state == null) return null;

        Inventory trailEffects = Bukkit.createInventory(null, 54, Component.text("Trail Effects"));

        for (Map.Entry<Integer, TrailEffect> entry : state.trailEffects.entrySet()) {
            TrailEffect effect = entry.getValue();
            trailEffects.setItem(entry.getKey(), createItemGUI(effect.invItem,
                    Component.text(effect.effectName, TextColor.fromHexString(effect.nameColor))
                            .decoration(TextDecoration.ITALIC, false)));
        }

        trailEffects.setItem(49, createItemGUI(Material.BARRIER,
                Component.text("Back", TextColor.fromHexString("#f60000"))
                        .decoration(TextDecoration.ITALIC, false)));
        state.active = trailEffects;
        state.activeType = 4;
        return trailEffects;
    }

    public void registerTeams(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        for (String team : teams) {
            Team checkTeam = scoreboard.getTeam(team);
            if (checkTeam != null) {
                checkTeam.unregister();
            }
        }
        scoreboard.registerNewTeam("blue").color(NamedTextColor.BLUE);
        scoreboard.registerNewTeam("red").color(NamedTextColor.RED);
        scoreboard.registerNewTeam("white").color(NamedTextColor.WHITE);
        scoreboard.registerNewTeam("aqua").color(NamedTextColor.AQUA);
        scoreboard.registerNewTeam("black").color(NamedTextColor.BLACK);
        scoreboard.registerNewTeam("dark_aqua").color(NamedTextColor.DARK_AQUA);
        scoreboard.registerNewTeam("dark_blue").color(NamedTextColor.DARK_BLUE);
        scoreboard.registerNewTeam("dark_gray").color(NamedTextColor.DARK_GRAY);
        scoreboard.registerNewTeam("dark_green").color(NamedTextColor.DARK_GREEN);
        scoreboard.registerNewTeam("dark_purple").color(NamedTextColor.DARK_PURPLE);
        scoreboard.registerNewTeam("dark_red").color(NamedTextColor.DARK_RED);
        scoreboard.registerNewTeam("gold").color(NamedTextColor.GOLD);
        scoreboard.registerNewTeam("gray").color(NamedTextColor.GRAY);
        scoreboard.registerNewTeam("green").color(NamedTextColor.GREEN);
        scoreboard.registerNewTeam("light_purple").color(NamedTextColor.LIGHT_PURPLE);
        scoreboard.registerNewTeam("yellow").color(NamedTextColor.YELLOW);
    }

    public void deRegisterTeams(Scoreboard scoreboard) {
        for (String team : teams) {
            Team checkTeam = scoreboard.getTeam(team);
            if (checkTeam != null) checkTeam.unregister();
        }
    }

    protected ItemStack createItemGUI(Material material, Component name) {
        ItemStack item = new ItemStack(material, 1);
        item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        item.setItemMeta(meta);
        return item;
    }

    protected ItemStack createArrowGUI(Material material, Component name, PotionType type) {
        if (material != Material.TIPPED_ARROW) return null;
        ItemStack item = new ItemStack(material, 1);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setBasePotionData(new PotionData(type));
        meta.displayName(name);
        item.setItemMeta(meta);
        item.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        return item;
    }

    public void processClick(int slot, UserState state, Player p) {
        if (!perms.has(p, "carrotcraft.donor")) return;
        String name = p.getName();
        if (slot == 49 && (state.activeType == 2 || state.activeType == 3 || state.activeType == 4)) {
            p.openInventory(openDonorGUI(name));
            return;
        }


        if (state.activeType == 1) { //main donor GUI
            if (slot == 19 && state.arrowEffectsEnabled) {
                p.openInventory(openArrowGUI(name));
            } else if (slot == 22 && state.glowEffectsEnabled) {
                p.openInventory(openGlowGUI(name));
            } else if (slot == 25 && state.trailEffectsEnabled) {
                p.openInventory(openTrailGUI(name));
            } else if (slot == 28 && state.arrowEffectsEnabled) {
                if (state.arrowEffect != null) {
                    state.arrowEffect = null;
                    p.closeInventory();
                }
            } else if (slot == 31 && state.glowEffectsEnabled) {
                if (state.glowing != null) {
                    scoreboard.getTeam(state.glowing.glow).removeEntry(name);
                    state.glowing = null;
                    p.setGlowing(false);
                    p.closeInventory();
                }
            } else if (slot == 34 && state.trailEffectsEnabled) {
                if (state.trailEffect != null) {
                    state.trailEffect = null;
                    p.closeInventory();
                }
            }
        } else if (state.activeType == 2 && state.arrowEffectsEnabled) { //arrow effects
            ArrowEffect effect = state.arrowEffects.get(slot);
            if (effect == null) return;
            if (state.arrowEffect != null && state.arrowEffect == effect) return;
            state.arrowEffect = effect;
            p.sendMessage(composeMsg(effect.effectName + " Arrows!", effect.nameColor));
        } else if (state.activeType == 3) { //glow effects
            GlowEffect effect = state.glowEffects.get(slot);
            if (effect == null) return;
            if (state.glowing != null) {
                if (state.glowing.equals(effect.glow)) return;
                scoreboard.getTeam(state.glowing.glow).removeEntry(name);
            }
            scoreboard.getTeam(effect.glow).addEntry(name);
            state.glowing = effect;
            p.setGlowing(true);
            p.sendMessage(composeMsg(effect.effectName + " Glow!", effect.nameColor));
        } else if (state.activeType == 4) { //trail effects
            TrailEffect effect = state.trailEffects.get(slot);
            if (effect == null) return;
            if (state.trailEffect != null && state.trailEffect == effect) return;
            state.trailEffect = effect;
            p.sendMessage(composeMsg(effect.effectName + " Trail!", effect.nameColor));
        }
    }

}
