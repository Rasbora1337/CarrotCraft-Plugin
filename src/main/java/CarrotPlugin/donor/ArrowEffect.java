package CarrotPlugin.donor;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionType;

public class ArrowEffect {
    public String effectName;
    public String nameColor;
    public PotionType potionType;
    public Material material;
    public Particle effect;
    public Integer count;
    public Integer speed;
    public Material trailMaterial;
    public Integer removeDelay;
    public long spawnDelay;

    public ArrowEffect(String effectName, String nameColor, PotionType potionType, Material material,
                       Particle effect, Integer count, Integer speed, Material trailMaterial,
                       Integer removeDelay, long spawnDelay
    ) {
        this.effectName = effectName;
        this.nameColor = nameColor;
        this.potionType = potionType;
        this.material = material;
        this.effect = effect;
        this.count = count;
        this.speed = speed;
        this.trailMaterial = trailMaterial;
        this.removeDelay = removeDelay;
        this.spawnDelay = spawnDelay;
    }
}
