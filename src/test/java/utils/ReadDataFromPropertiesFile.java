package utils;

import org.testng.annotations.BeforeSuite;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ReadDataFromPropertiesFile {

    public static String username;
    public static String password;
    public static String baseUrl;

    @BeforeSuite
    public static void fetchData() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/configData/configData.properties");
        Properties p = new Properties();
        p.load(fileInputStream);

        username = p.getProperty("username");
        password = p.getProperty("password");
        baseUrl = p.getProperty("baseUrl");
    }

}
