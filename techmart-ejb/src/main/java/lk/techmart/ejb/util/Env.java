package lk.techmart.ejb.util;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import lk.techmart.core.service.ConfigService;

import java.io.InputStream;
import java.security.PrivateKey;
import java.util.Properties;

@Startup
@Singleton
public class Env implements ConfigService {
    private Properties properties;

    @PostConstruct
    public void init(){
        properties = new Properties();
        try(InputStream inputStream =
                getClass().getClassLoader().getResourceAsStream("app.properties")){

            if (inputStream == null){
                throw new RuntimeException("app.properties not found");
            }
            properties.load(inputStream);

        } catch (Exception e) {
            throw new RuntimeException("app.properties loading failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String get(String key) {
        return properties.getProperty(key);
    }

}
