package com.example.databroker.plugin;

import com.example.databroker.dto.Message;
import com.example.databr
oker.service.DataBroker;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

@Component
public class JarPluginLoader implements MessageProcessor {
    private final DataBroker dataBroker;

    public JarPluginLoader(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    @Override
    public boolean canHandle(Message message) {
        return "load_jar".equals(message.getType());
    }

    @Override
    public Object process(Message message) {
        String dir = (String) message.getPayload("dir", "plugins");
        try {
            File pluginDir = new File(dir);
            if (!pluginDir.exists()) return "Plugin directory not found: " + dir;
            List<MessageProcessor> newPlugins = new ArrayList<>();
            for (File jar : pluginDir.listFiles(f -> f.getName().endsWith(".jar"))) {
                try (JarFile jarFile = new JarFile(jar)) {
                    URL jarUrl = jar.toURI().toURL();
                    URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl}, getClass().getClassLoader());
                    jarFile.entries().asIterator().forEachRemaining(entry -> {
                        if (entry.getName().endsWith(".class")) {
                            try {
                                String className = entry.getName().replace("/", ".").replace(".class", "");
                                Class<?> clazz = loader.loadClass(className);
                                if (MessageProcessor.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                                    newPlugins.add((MessageProcessor) clazz.getDeclaredConstructor().newInstance());
                                }
                            } catch (Exception e) {
                                // Skip invalid class, continue
                            }
                        }
                    });
                }
            }
            newPlugins.forEach(dataBroker::registerPlugin);
            return "Loaded " + newPlugins.size() + " plugins successfully";
        } catch (Exception e) {
            return "Failed to load JARs: " + e.getMessage();
        }
    }
}

