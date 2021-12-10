package CarrotPlugin.donor;

import org.bukkit.Material;
import org.bukkit.Particle;

public class TrailEffect {
    public String effectName;
    public String nameColor;
    public Material invItem;
    public Particle trail;
    public Integer count;
    public Integer speed;
    public Material trailMaterial;
    public Integer removeDelay;
    public long spawnDelay;

    public TrailEffect(String effectName, String nameColor, Material invItem,
                       Particle trail, Integer count, Integer speed, Material trailMaterial,
                       Integer removeDelay, long spawnDelay) {
        this.effectName = effectName;
        this.nameColor = nameColor;
        this.invItem = invItem;
        this.trail = trail;
        this.count = count;
        this.speed = speed;
        this.trailMaterial = trailMaterial;
        this.removeDelay = removeDelay;
        this.spawnDelay = spawnDelay;
    }
}