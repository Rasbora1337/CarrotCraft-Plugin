package CarrotPlugin.DB;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionType;

import java.util.Map;

import static java.util.Map.entry;

public class Maps {
    public Map<String, PotionType> potionTypeMap = Map.ofEntries(
            entry("FIRE_RESISTANCE", PotionType.FIRE_RESISTANCE),
            entry("REGEN", PotionType.REGEN),
            entry("NIGHT_VISION", PotionType.NIGHT_VISION),
            entry("INVISIBILITY", PotionType.INVISIBILITY),
            entry("JUMP", PotionType.JUMP),
            entry("SPEED", PotionType.SPEED),
            entry("SLOWNESS", PotionType.SLOWNESS),
            entry("TURTLE_MASTER", PotionType.TURTLE_MASTER),
            entry("WATER_BREATHING", PotionType.WATER_BREATHING),
            entry("INSTANT_HEAL", PotionType.INSTANT_HEAL),
            entry("INSTANT_DAMAGE", PotionType.INSTANT_DAMAGE),
            entry("POISON", PotionType.POISON),
            entry("STRENGTH", PotionType.STRENGTH),
            entry("WEAKNESS", PotionType.WEAKNESS),
            entry("LUCK", PotionType.LUCK),
            entry("SLOW_FALLING", PotionType.SLOW_FALLING)
    );

    public Map<String, Particle> particleMap = Map.ofEntries(
            entry("DRIP_LAVA", Particle.DRIP_LAVA),
            entry("TOTEM", Particle.TOTEM),
            entry("NOTE", Particle.NOTE),
            entry("ITEM_CRACK", Particle.ITEM_CRACK),
            entry("EXPLOSION_NORMAL", Particle.EXPLOSION_NORMAL),
            entry("BARRIER", Particle.BARRIER),
            entry("BLOCK_CRACK", Particle.BLOCK_CRACK),
            entry("WATER_BUBBLE", Particle.WATER_BUBBLE),
            entry("BUBBLE_COLUMN_UP", Particle.BUBBLE_COLUMN_UP),
            entry("CAMPFIRE_COSY_SMOKE", Particle.CAMPFIRE_COSY_SMOKE),
            entry("CAMPFIRE_SIGNAL_SMOKE", Particle.CAMPFIRE_SIGNAL_SMOKE),
            entry("CLOUD", Particle.CLOUD),
            entry("COMPOSTER", Particle.COMPOSTER),
            entry("CRIMSON_SPORE", Particle.CRIMSON_SPORE),
            entry("CRIT", Particle.CRIT),
            entry("CURRENT_DOWN", Particle.CURRENT_DOWN),
            entry("DAMAGE_INDICATOR", Particle.DAMAGE_INDICATOR),
            entry("DOLPHIN", Particle.DOLPHIN),
            entry("DRAGON_BREATH", Particle.DRAGON_BREATH),
            entry("DRIP_WATER", Particle.DRIP_WATER),
            entry("DRIPPING_HONEY", Particle.DRIPPING_HONEY),
            entry("DRIPPING_OBSIDIAN_TEAR", Particle.DRIPPING_OBSIDIAN_TEAR),
            entry("END_ROD", Particle.END_ROD),
            entry("FLAME", Particle.FLAME),
            entry("HEART", Particle.HEART)

    );

    public Map<String, Material> materialMap = Map.ofEntries(
            entry("WHITE_WOOL", Material.WHITE_WOOL),
            entry("RED_WOOL", Material.RED_WOOL),
            entry("YELLOW_WOOL", Material.YELLOW_WOOL),
            entry("BLACK_WOOL", Material.BLACK_WOOL),
            entry("ORANGE_WOOL", Material.ORANGE_WOOL),
            entry("BLUE_WOOL", Material.BLUE_WOOL),
            entry("GREEN_WOOL", Material.GREEN_WOOL),
            entry("PURPLE_WOOL", Material.PURPLE_WOOL),
            entry("GRAY_WOOL", Material.GRAY_WOOL),
            entry("LIGHT_GRAY_WOOL", Material.LIGHT_GRAY_WOOL),
            entry("LIGHT_BLUE_WOOL", Material.LIGHT_BLUE_WOOL),
            entry("MAGENTA_WOOL", Material.MAGENTA_WOOL),
            entry("CYAN_WOOL", Material.CYAN_WOOL),
            entry("LIME_WOOL", Material.LIME_WOOL),
            entry("PINK_WOOL", Material.PINK_WOOL),
            entry("CARROT", Material.CARROT),
            entry("BROWN_MUSHROOM", Material.BROWN_MUSHROOM),
            entry("RED_MUSHROOM", Material.RED_MUSHROOM),
            entry("TORCH", Material.TORCH),
            entry("COAL", Material.COAL),
            entry("DIAMOND", Material.DIAMOND),
            entry("IRON_INGOT", Material.IRON_INGOT),
            entry("GOLD_INGOT", Material.GOLD_INGOT),
            entry("FEATHER", Material.FEATHER),
            entry("GUNPOWDER", Material.GUNPOWDER),
            entry("FLINT", Material.FLINT),
            entry("WATER_BUCKET", Material.WATER_BUCKET),
            entry("LAVA_BUCKET", Material.LAVA_BUCKET),
            entry("REDSTONE", Material.REDSTONE),
            entry("SNOWBALL", Material.SNOWBALL),
            entry("BRICK", Material.BRICK),
            entry("BOOK", Material.BOOK),
            entry("KELP", Material.KELP),
            entry("SLIME_BALL", Material.SLIME_BALL),
            entry("EGG", Material.EGG),
            entry("LAPIS_LAZULI", Material.LAPIS_LAZULI),
            entry("ENDER_PEARL", Material.ENDER_PEARL),
            entry("GHAST_TEAR", Material.GHAST_TEAR),
            entry("NOTE_BLOCK", Material.NOTE_BLOCK),
            entry("BARRIER", Material.BARRIER),
            entry("TOTEM_OF_UNDYING", Material.TOTEM_OF_UNDYING),
            entry("HONEYCOMB_BLOCK", Material.HONEYCOMB_BLOCK),
            entry("TNT", Material.TNT),
            entry("CAMPFIRE", Material.CAMPFIRE),
            entry("GOLDEN_CARROT", Material.GOLDEN_CARROT),
            entry("ENCHANTED_GOLDEN_APPLE", Material.ENCHANTED_GOLDEN_APPLE),
            entry("IRON_SWORD", Material.IRON_SWORD),
            entry("SALMON", Material.SALMON),
            entry("WHITE_DYE", Material.WHITE_DYE),
            entry("FIRE_CHARGE", Material.FIRE_CHARGE),
            entry("CRYING_OBSIDIAN", Material.CRYING_OBSIDIAN),
            entry("DRAGON_BREATH", Material.DRAGON_BREATH),
            entry("NETHERITE_SWORD", Material.NETHERITE_SWORD),
            entry("AXOLOTL_BUCKET", Material.AXOLOTL_BUCKET),
            entry("CREEPER_HEAD", Material.CREEPER_HEAD),
            entry("DRAGON_HEAD", Material.DRAGON_HEAD)
    );
}
