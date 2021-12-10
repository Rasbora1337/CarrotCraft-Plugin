package CarrotPlugin.DB;

import CarrotPlugin.donor.ArrowEffect;
import CarrotPlugin.donor.GlowEffect;
import CarrotPlugin.donor.TrailEffect;
import CarrotPlugin.donor.UserState;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class DB {
    HikariDataSource ds;
    Plugin plugin;
    BidiMap<Integer, ArrowEffect> arrowEffects;
    BidiMap<Integer, GlowEffect> glowEffects;
    BidiMap<Integer, TrailEffect> trailEffects;
    Logger log;
    Scoreboard scoreboard;
    Maps maps = new Maps();
    Map<String, UserState> donors;
    Set<String> donorNames;

    public DB(File pluginDir, Plugin carrotPlugin, Logger log, Map<String, UserState> donors, Scoreboard scoreboard, Set<String> donorNames) {
        this.plugin = carrotPlugin;
        this.log = log;
        this.donors = donors;
        this.scoreboard = scoreboard;
        this.donorNames = donorNames;

        HikariConfig config = new HikariConfig(pluginDir + "/hikari.properties");
        ds = new HikariDataSource(config);

        arrowEffects = loadArrowEffects();
        glowEffects = loadGlowEffects();
        trailEffects = loadTrailEffects();
    }

    public void onClose() {
        ds.close();
    }

    public HashMap loadStats() {
        try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * from `stats`")) {
            ResultSet stats = stmt.executeQuery();
            HashMap<String, Integer> carrotCounts = new HashMap<String, Integer>();
            while (stats.next()) {
                String user = stats.getString("user");
                Integer count = stats.getInt("carrots");
                if (user != null && count != null) carrotCounts.put(user, count);
            }
            return carrotCounts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<String, String> loadUsers() {
        try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * from `users`")) {
            ResultSet fetchUsers = stmt.executeQuery();
            HashMap<String, String> users = new HashMap<>();
            while (fetchUsers.next()) {
                String name = fetchUsers.getString("name");
                String discordID = fetchUsers.getString("discordID");
                if (name != null) {
                    if (discordID != null) users.put(name, discordID);
                    Integer isDonor = fetchUsers.getInt("donor");
                    if (isDonor > 0) donorNames.add(name);
                }
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateStats(HashMap<String, Integer> stats) {
        HashMap<String, Integer> currentData = loadStats();
        if (currentData == null) {
            System.out.println("Could not load current data for stat updater!");
            return;
        }

        try (Connection conn = ds.getConnection()) {
            for (Map.Entry<String, Integer> stat : stats.entrySet()) {
                Integer current = currentData.get(stat.getKey());
                if (current == null) {
                    PreparedStatement insertUser = conn.prepareStatement("INSERT INTO `stats` (user, carrots) VALUES(?, ?)");
                    insertUser.setString(1, stat.getKey());
                    insertUser.setInt(2, stat.getValue());
                    insertUser.execute();
                    if (insertUser.getUpdateCount() != 1)
                        System.out.println("ERROR Inserting user data! - " + stat.getKey() + " - " + stat.getValue());
                } else {
                    if (current < stat.getValue()) {
                        PreparedStatement updateUser = conn.prepareStatement("UPDATE `stats` SET `carrots` = ? WHERE `user` = ?");
                        updateUser.setInt(1, stat.getValue());
                        updateUser.setString(2, stat.getKey());
                        updateUser.execute();
                        if (updateUser.getUpdateCount() != 1)
                            System.out.println("ERROR updating user data! - " + stat.getKey() + " - " + stat.getValue());
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void insertUser(final String name, final String discordID, final DB_Callback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                        "SET @name = ?, @discordID = ?;\n" +
                                "INSERT INTO `users` (`name`, `discordID`) VALUES(@name, @discordID)\n" +
                                "ON DUPLICATE KEY UPDATE `discordID` = @discordID")) {
                    stmt.setString(1, name);
                    stmt.setString(2, discordID);
                    stmt.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(true);
                    }
                });
            }
        });

    }

    public BidiMap<Integer, ArrowEffect> loadArrowEffects() {
        try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `arrowEffects`")) {
            ResultSet fetchEffects = stmt.executeQuery();
            BidiMap<Integer, ArrowEffect> effects = new DualHashBidiMap<>();
            while (fetchEffects.next()) {
                int ID = fetchEffects.getInt("ID");
                String name = fetchEffects.getString("name");
                String color = fetchEffects.getString("color");
                String type = fetchEffects.getString("type");
                String material = fetchEffects.getString("material");
                String particle = fetchEffects.getString("particle");
                long spawnDelay = fetchEffects.getLong("spawnDelay");
                if (ID == 0 || name == null || color == null || (type == null && material == null) || particle == null || spawnDelay == 0) {
                    log.severe("Null column loading arrow effects!");
                    continue;
                }

                PotionType potionType = null;
                Material invMaterial = null;
                if (type != null) {
                    potionType = maps.potionTypeMap.get(type);
                    if (potionType == null) {
                        log.severe("Could not map PotionType " + type + "!!!");
                        continue;
                    }
                } else if (material != null) {
                    invMaterial = maps.materialMap.get(material);
                    if (invMaterial == null) {
                        log.severe("Could not map invMaterial " + material + "!!!");
                        continue;
                    }
                }


                Particle particleType = maps.particleMap.get(particle);
                if (particleType == null) {
                    log.severe("Could not map Particle " + particle + "!!!");
                    continue;
                }

                String trailMaterial = fetchEffects.getString("trailMaterial");
                Material trailMaterialType = null;
                Integer removeDelay = 0;
                if (trailMaterial != null) {
                    trailMaterialType = maps.materialMap.get(trailMaterial);
                    if (trailMaterialType == null) {
                        log.severe("Could not map trailMaterial " + trailMaterial + "!!!");
                        continue;
                    }

                    removeDelay = fetchEffects.getInt("removeDelay");
                    if (removeDelay == 0) {
                        log.severe("Invalid removeDelay for arrowEffect ID " + ID + "!!!");
                        continue;
                    }

                    if (particleType != Particle.ITEM_CRACK) {
                        log.severe("ParticleType isnt ITEM CRACK for arrowEffect ID " + ID + "!!!");
                        continue;
                    }
                }

                if (particleType == Particle.ITEM_CRACK && trailMaterialType == null) {
                    log.severe("ParticleType is ITEM CRACK and trailMaterialType is NULL for arrowEffect ID " + ID + "!!!");
                    continue;
                }


                Integer count = fetchEffects.getInt("count");
                Integer speed = fetchEffects.getInt("speed");

                effects.put(ID, new ArrowEffect(name, color, potionType, invMaterial,
                        particleType, count, speed, trailMaterialType,
                        removeDelay, spawnDelay));

            }
            return effects;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BidiMap<Integer, GlowEffect> loadGlowEffects() {
        try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `glowEffects`")) {
            ResultSet fetchEffects = stmt.executeQuery();
            BidiMap<Integer, GlowEffect> effects = new DualHashBidiMap<>();
            while (fetchEffects.next()) {
                int ID = fetchEffects.getInt("ID");
                String name = fetchEffects.getString("name");
                String color = fetchEffects.getString("color");
                String material = fetchEffects.getString("material");
                String glow = fetchEffects.getString("glow");
                if (ID == 0 || name == null || color == null || material == null || glow == null) {
                    log.severe("Null column loading glow effects!");
                    continue;
                }

                Material materialType = maps.materialMap.get(material);
                if (materialType == null) {
                    log.severe("Could not map material " + material + "!!!");
                    continue;
                }

                effects.put(ID, new GlowEffect(name, color, materialType, glow));

            }
            return effects;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BidiMap<Integer, TrailEffect> loadTrailEffects() {
        try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `trailEffects`")) {
            ResultSet fetchEffects = stmt.executeQuery();
            BidiMap<Integer, TrailEffect> effects = new DualHashBidiMap<>();
            while (fetchEffects.next()) {
                int ID = fetchEffects.getInt("ID");
                String name = fetchEffects.getString("name");
                String color = fetchEffects.getString("color");
                String material = fetchEffects.getString("material");
                String particle = fetchEffects.getString("particle");
                long spawnDelay = fetchEffects.getLong("spawnDelay");
                if (ID == 0 || name == null || color == null || material == null || particle == null || spawnDelay == 0) {
                    log.severe("Null column loading trail effects!");
                    continue;
                }

                Material materialType = maps.materialMap.get(material);
                if (materialType == null) {
                    log.severe("Could not map material " + material + "!!!");
                    continue;
                }

                Particle particleType = maps.particleMap.get(particle);
                if (particleType == null) {
                    log.severe("Could not map particle " + particle + "!!!");
                    continue;
                }

                String trailMaterial = fetchEffects.getString("trailMaterial");
                Material trailMaterialType = null;
                Integer removeDelay = 0;
                if (trailMaterial != null) {
                    trailMaterialType = maps.materialMap.get(trailMaterial);
                    if (trailMaterialType == null) {
                        log.severe("Could not map trailMaterial " + trailMaterial + "!!!");
                        continue;
                    }

                    removeDelay = fetchEffects.getInt("removeDelay");
                    if (removeDelay == 0) {
                        log.severe("Invalid removeDelay for trailEffect ID " + ID + "!!!");
                        continue;
                    }

                    if (particleType != Particle.ITEM_CRACK) {
                        log.severe("ParticleType isnt ITEM CRACK for trailEffect ID " + ID + "!!!");
                        continue;
                    }
                }

                if (particleType == Particle.ITEM_CRACK && trailMaterialType == null) {
                    log.severe("ParticleType is ITEM CRACK and trailMaterialType is NULL for trailEffect ID " + ID + "!!!");
                    continue;
                }


                Integer count = fetchEffects.getInt("count");
                Integer speed = fetchEffects.getInt("speed");

                effects.put(ID, new TrailEffect(name, color, materialType,
                        particleType, count, speed, trailMaterialType,
                        removeDelay, spawnDelay));

            }
            return effects;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadDonor(Player p, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                        "SELECT userArrowEffects.effect AS arrowEffect, userGlowEffects.effect as glowEffect, userTrailEffects.effect as trailEffect,\n" +
                                "state.`arrowEffect` as activeArrowEffect, state.`glowing` as activeGlowEffect, state.`trailEffect` as activeTrailEffect \n" +
                                "FROM users\n" +
                                "LEFT JOIN userArrowEffects\n" +
                                "ON users.ID = userArrowEffects.user\n" +
                                "LEFT JOIN userGlowEffects\n" +
                                "ON users.ID = userGlowEffects.user\n" +
                                "LEFT JOIN userTrailEffects\n" +
                                "ON users.ID = userTrailEffects.user\n" +
                                "LEFT JOIN state ON users.`name` = state.`user`\n" +
                                "WHERE users.`donor` > 0 AND users.`name` = ?"
                )) {
                    stmt.setString(1, name);
                    ResultSet fetchUserEffects = stmt.executeQuery();
                    UserState state = new UserState();
                    Integer activeArrowEffect = 0;
                    Integer activeGlowingEffect = 0;
                    Integer activeTrailEffect = 0;
                    while (fetchUserEffects.next()) {
                        Integer arrowEffect = fetchUserEffects.getInt("arrowEffect");
                        if (arrowEffect != 0) {
                            ArrowEffect getArrowEffect = arrowEffects.get(arrowEffect);
                            if (getArrowEffect == null) {
                                log.severe("Could not map arrowEffect in loadDonor!!! - " + arrowEffect);
                            } else {
                                if (!state.arrowEffects.containsValue(getArrowEffect))
                                    state.arrowEffects.put(state.arrowEffects.size(), getArrowEffect);
                            }
                        }

                        Integer glowEffect = fetchUserEffects.getInt("glowEffect");
                        if (glowEffect != 0) {
                            GlowEffect getGlowEffect = glowEffects.get(glowEffect);
                            if (getGlowEffect == null) {
                                log.severe("Could not map glowEffect in loadDonor!!! - " + glowEffect);
                            } else {
                                if (!state.glowEffects.containsValue(getGlowEffect))
                                    state.glowEffects.put(state.glowEffects.size(), getGlowEffect);
                            }
                        }

                        Integer trailEffect = fetchUserEffects.getInt("trailEffect");
                        if (trailEffect != 0) {
                            TrailEffect getTrailEffect = trailEffects.get(trailEffect);
                            if (getTrailEffect == null) {
                                log.severe("Could not map trailEffect in loadDonor!!! - " + trailEffect);
                            } else {
                                if (!state.trailEffects.containsValue(getTrailEffect))
                                    state.trailEffects.put(state.trailEffects.size(), getTrailEffect);
                            }
                        }

                        activeArrowEffect = fetchUserEffects.getInt("activeArrowEffect");
                        activeGlowingEffect = fetchUserEffects.getInt("activeGlowEffect");
                        activeTrailEffect = fetchUserEffects.getInt("activeTrailEffect");
                    }

                    if (activeArrowEffect != 0) {
                        ArrowEffect activeEffect = arrowEffects.get(activeArrowEffect);
                        if (activeEffect != null && state.arrowEffects.containsValue(activeEffect)) {
                            state.arrowEffect = activeEffect;
                        }
                    }

                    if (activeGlowingEffect != 0) {
                        GlowEffect activeEffect = glowEffects.get(activeGlowingEffect);
                        if (activeEffect != null && state.glowEffects.containsValue(activeEffect)) {
                            state.glowing = activeEffect;
                        }
                    }

                    if (activeTrailEffect != 0) {
                        TrailEffect activeEffect = trailEffects.get(activeTrailEffect);
                        if (activeEffect != null && state.trailEffects.containsValue(activeEffect)) {
                            state.trailEffect = activeEffect;
                        }
                    }

                    if (state.glowEffects.size() > 0) state.glowEffectsEnabled = true;
                    if (state.arrowEffects.size() > 0) state.arrowEffectsEnabled = true;
                    if (state.trailEffects.size() > 0) state.trailEffectsEnabled = true;

                    donors.put(name, state);

                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            if (state.glowing != null) {
                                p.setGlowing(true);
                                scoreboard.getTeam(state.glowing.glow).addEntry(name);
                            }
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void saveDonorState(String name, UserState currentState) {
        if (currentState == null) return;
        Integer arrowEffect = arrowEffects.getKey(currentState.arrowEffect);
        Integer glowEffect = glowEffects.getKey(currentState.glowing);
        Integer trailEffect = trailEffects.getKey(currentState.trailEffect);

        try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SET @user = ?, @arrowEffect = ?, @glowing = ?, @trailEffect = ?;\n" +
                        "INSERT INTO `state` (`user`, `arrowEffect`, `glowing`, `trailEffect`) VALUES(@user, @arrowEffect, @glowing, @trailEffect)\n" +
                        "ON DUPLICATE KEY UPDATE `arrowEffect` = @arrowEffect, `glowing` = @glowing, `trailEffect` = @trailEffect"
        )) {
            stmt.setString(1, name);
            if (arrowEffect == null) {
                stmt.setNull(2, Types.NULL);
            } else {
                stmt.setInt(2, arrowEffect);
            }

            if (glowEffect == null) {
                stmt.setNull(3, Types.NULL);
            } else {
                stmt.setInt(3, glowEffect);
            }

            if (trailEffect == null) {
                stmt.setNull(4, Types.NULL);
            } else {
                stmt.setInt(4, trailEffect);
            }


            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
