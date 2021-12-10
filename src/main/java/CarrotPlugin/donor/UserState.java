package CarrotPlugin.donor;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class UserState {
    public Inventory active;
    public ArrowEffect arrowEffect;
    public GlowEffect glowing;
    public TrailEffect trailEffect;
    public long lastSpawn;
    public int activeType;
    public Location lastLoc;
    public boolean arrowEffectsEnabled;
    public boolean glowEffectsEnabled;
    public boolean trailEffectsEnabled;
    public Map<Integer, ArrowEffect> arrowEffects = new HashMap<>();
    public Map<Integer, GlowEffect> glowEffects = new HashMap<>();
    public Map<Integer, TrailEffect> trailEffects = new HashMap<>();

}
