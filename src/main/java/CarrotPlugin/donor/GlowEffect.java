package CarrotPlugin.donor;

import org.bukkit.Material;

public class GlowEffect {
    public String effectName;
    public String nameColor;
    public Material invItem;
    public String glow;

    public GlowEffect(String effectName, String nameColor, Material invItem, String glow) {
        this.effectName = effectName;
        this.nameColor = nameColor;
        this.invItem = invItem;
        this.glow = glow;
    }
}
