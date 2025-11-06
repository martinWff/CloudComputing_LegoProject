package cc.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {

    private static String db_endpoint=null;
    private static String db_key=null;
    private static String db_name=null;
    private static String text_analytic_endpoint=null;
    private static String text_analytic_key=null;

    private static String redis_hostname;

    private static String redis_key;

    private static Map<String,String> map;

    public static Map<String, String> load(String filePath) {
        Map<String, String> env = new HashMap<>();
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println(".env file not found at: " + file.getAbsolutePath());
            return env;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                int idx = line.indexOf('=');
                if (idx != -1) {
                    String key = line.substring(0, idx).trim();
                    String value = line.substring(idx + 1).trim();

                    // Remove optional surrounding quotes
                    if ((value.startsWith("\"") && value.endsWith("\"")) ||
                        (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }

                    env.put(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return env;
    }

    public static void init()
    {
        map = EnvLoader.load(".env");
    }

    public static String getVariable(String name) {
        return map.get(name);
    }

    public static boolean hasVariable(String name) {
        return map.containsKey(name);
    }
}
