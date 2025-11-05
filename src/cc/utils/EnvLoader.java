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
    private static String img_container=null;
    private static String img_container_tk=null;
    private static String text_analytic_endpoint=null;
    private static String text_analytic_key=null;

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

    public static void envInit()
    {
        Map<String, String> env = EnvLoader.load(".env");
        db_endpoint = env.get("db_endpoint");
        db_key = env.get("db_key");
        db_name = env.get("db_name");
        img_container = env.get("img_container");
        img_container_tk = env.get("img_container_tk");
        text_analytic_endpoint=env.get("text_analytic_endpoint");
        text_analytic_key=env.get("text_analytic_key");
        //System.out.println("Host: " + dbHost);
        //System.out.println("User: " + dbUser);
    }

    public static String GetDBKeys()
    {
        return db_key;
    }

    public static String GetDBEndpoint()
    {
        return db_endpoint;
    }

    public static String GetDBName()
    {
        return db_name;
    }

    public static String GetImg_Container_Tk()
    {
        return img_container_tk;
    }

    public static String GetImg_Container()
    {
        return img_container;
    }

     public static String GetTextAnalyticEndpoint()
    {
        return text_analytic_endpoint;
    }

    public static String GetTextAnalyticKey()
    {
        return text_analytic_key;
    }
}
