package CarrotPlugin;

import CarrotPlugin.DB.DB;

import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

public class Counter {
    public HashMap<String, Integer> carrotCounts;
    DB DB;

    public Counter(File pluginDir, DB DB) {
        this.DB = DB;
        carrotCounts = this.DB.loadStats();
        if (carrotCounts == null) {
            System.out.println("COULD NOT LOAD CARROT STATS OH SHIT WTF test");
        }

       /* try {
            String actual = Files.readString(Path.of(pluginDir + "/counter.json"));
            Type mapType = new TypeToken<HashMap<String, Integer>>() {
            }.getType();
            carrotCounts = new Gson().fromJson(actual, mapType);
        } catch (Exception e) {
            System.out.println("An error occurred loading counter config!");
            e.printStackTrace();
        }


        for (Map.Entry<String, Integer> meme : carrotCounts.entrySet()) {
            this.DB.insertData(meme.getKey(), meme.getValue(), new DB_Callback() {
                @Override
                public void onQueryDone(boolean result) {
                    if (!result) {
                        System.out.println("Failure inserting! " + meme.getKey());
                    }
                }
            });
        }*/
    }


    public int increment(String username) {
        carrotCounts.putIfAbsent(username, 0);
        int newCount = carrotCounts.get(username) + 1;
        carrotCounts.put(username, newCount);
        return newCount;
    }

    public String getCount(String name) {
        Integer count = carrotCounts.get(name);
        if (count != null) return NumberFormat.getNumberInstance(Locale.US).format(count);
        return null;
    }

}
