package net.blazinblaze.config;

import net.blazinblaze.BlazinHideCoordinates;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class CoordinatesYAML {

    public static final String shouldHideCoordinates = "shouldHideCoordinates";
    public static final String hideYCoordinates = "hideYCoordinates";
    public static final String configCoordinatesKey = "hideCoordinatesConfig";

    private final File file;
    private Map<String, Map<String, Object>> config = new LinkedHashMap<String, Map<String, Object>>();
    private Map<String, Object> coordinatesConfig = new LinkedHashMap<String, Object>();

    private DumperOptions yamlOptions = new DumperOptions();
    private Yaml yaml;

    private void setDefaults() {
        coordinatesConfig.put(shouldHideCoordinates, true);
        coordinatesConfig.put(hideYCoordinates, false);

        save();
    }

    public void save() {
        config.put(configCoordinatesKey, coordinatesConfig);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            yaml.dump(config, writer);
        } catch(IOException | YAMLException e) {
            BlazinHideCoordinates.LOGGER.error("Error while saving yaml config file.", e);
        }
    }

    public void load() {
        if(file.exists()) {
            try {
                InputStream stream = new FileInputStream(file.getAbsolutePath());
                Map<String, Map<String, Object>> data = yaml.load(stream);
                coordinatesConfig = data.get(configCoordinatesKey);
            }catch(FileNotFoundException e) {
                BlazinHideCoordinates.LOGGER.error("Config file not found.", e);
            }
        }else {
            createNew();
        }
    }

    public void setCoordinateVal(String key, Object val) {
        if(key.equals(shouldHideCoordinates) || key.equals(hideYCoordinates)) {
            if(val instanceof Boolean) {
                coordinatesConfig.put(key, val);
            }
        }
    }

    public Object getCoordinateVal(String key) {
        Object temp = coordinatesConfig.get(key);
        if(temp instanceof Boolean) {
            return temp;
        }else {
            return null;
        }
    }

    public CoordinatesYAML(File file) {
        this.file = file;
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // For block style YAML
        yamlOptions.setPrettyFlow(true);
        yamlOptions.setProcessComments(true);
        yaml = new Yaml(yamlOptions);
    }

    private void createNew() {
        setDefaults();
        save();
    }

}
