package io.oreto.toil;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Toil {
    public static class Config {
       private final Map<String, Object> _properties = new HashMap<>();

       public Config host(String host) {
           return set("host", host);
       }

        public Config port(String port) {
            return set("port", port);
        }

        public Config db(String db) {
            return set("db", db);
        }

        public Config user(String user) {
            return set("user", user);
        }

        public Config pass(String pass) {
            return set("pass", pass);
        }

        public Config set(String property, Object value) {
           _properties.put(property, value);
           return this;
        }

        public Properties getProperties() {
           Properties properties = new Properties();
           _properties.forEach((k, v) -> properties.setProperty(k, v == null ? null : v.toString()));
           return properties;
        }
    }
    public static class JsonProvider {
        private static final Curator<ObjectMapper> INSTANCE = Curator.lazyDefault(ObjectMapper::new);
        // throws exception if instance is already set to a different value
        // Prevents any mutations from happening after the first value is set
        static void set(ObjectMapper mapper) { INSTANCE.set(mapper); }
        public static ObjectMapper get() { return INSTANCE.get(); }
    }
}
