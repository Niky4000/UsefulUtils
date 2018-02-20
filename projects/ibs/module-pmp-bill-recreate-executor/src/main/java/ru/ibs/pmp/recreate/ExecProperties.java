package ru.ibs.pmp.recreate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import static org.springframework.beans.factory.config.PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK;

/**
 *
 * @author NAnishhenko
 */
public class ExecProperties extends PropertyPlaceholderConfigurer {

    public static final String MODULE_PORT = "module.port";
    public static final String MODULE_HOST = "module.host";
    public static final String PMP_CLUSTER_NAME = "pmp.cluster.name";
    public static final String PMP_CLUSTER_CONFIG = "pmp.cluster.config";

    private static Map<String, String> properties;

    private int springSystemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;

    @Override
    public void setSystemPropertiesMode(int systemPropertiesMode) {
        super.setSystemPropertiesMode(systemPropertiesMode);
        springSystemPropertiesMode = systemPropertiesMode;
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
            throws BeansException {
        super.processProperties(beanFactoryToProcess, props);

        properties = new LinkedHashMap<>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String valStr = resolvePlaceholder(keyStr, props, springSystemPropertiesMode);
            properties.put(keyStr, valStr);
        }
    }

    public static String getProperty(String key) {
        if (properties == null) {
            throw new IllegalStateException("You should use PmpProperties as spring propertyConfigurer bean "
                    + "with at least 'common.properties' location");
        }
        String propValue = properties.get(key);
        if (propValue != null && propValue.startsWith("${")) {
            propValue = propValue.substring(2, propValue.length() - 1);
            propValue = properties.get(propValue);
        }
        return propValue;
    }

}
