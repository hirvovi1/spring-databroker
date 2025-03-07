package com.example.databroker.plugin;

import com.example.databroker.dto.Message;
import com.example.databroker.service.DataBroker;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Logger;

@Component
public class JarPluginLoader implements MessageProcessor {
    private final DataBroker dataBroker;
    private static final Logger logger = Logger.getLogger(JarPluginLoader.class.getName());

    public JarPluginLoader(@Lazy DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    @Override
    public boolean canHandle(Message message) {
        return "load_jar".equals(message.getType());
    }

    @Override
    public Object process(Message message) {
        String dir = (String) message.getPayload("dir", "plugins");
        logger.info("Starting to load JAR plugins from directory: " + dir);
        try {
            File pluginDir = new File(dir);
            if (!pluginDir.exists()) {
                logger.warning("Plugin directory does not exist: " + dir);
                return "Plugin directory not found: " + dir;
            }
            List<MessageProcessor> newPlugins = new ArrayList<>();
            File[] jarFiles = pluginDir.listFiles(f -> f.getName().endsWith(".jar"));
            if (jarFiles == null || jarFiles.length == 0) {
                logger.info("No JAR files found in directory: " + dir);
                return "No JAR files found in directory: " + dir;
            }
            for (File jar : jarFiles) {
                logger.fine("Processing JAR file: " + jar.getName());
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
                                    logger.fine("Loaded plugin class: " + className);
                                }
                            } catch (Exception e) {
                                logger.warning("Failed to load class " + entry.getName() + " from JAR " + jar.getName() + ": " + e.getMessage());
                                // Skip invalid class, continue
                            }
                        }
                    });
                } catch (Exception e) {
                    logger.severe("Failed to process JAR file " + jar.getName() + ": " + e.getMessage());
                }
            }
            newPlugins.forEach(dataBroker::registerPlugin);
            logger.info("Loaded " + newPlugins.size() + " plugins successfully from directory: " + dir);
            return "Loaded " + newPlugins.size() + " plugins successfully";
        } catch (Exception e) {
            logger.severe("Failed to load JARs from directory " + dir + ": " + e.getMessage());
            return "Failed to load JARs: " + e.getMessage();
        }
    }
}

