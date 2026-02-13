package Parsing;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wpi.first.wpilibj.Filesystem;

import java.io.File;
import java.nio.file.Path;

public class ConfigLoader {

    private static final ObjectMapper mapper = new ObjectMapper();

    // load from file path (e.g., deploy/configs/swerve.json)
    public static <T> T load(File f, Class<T> clazz) throws Exception {
        T cfg = mapper.readValue(f, clazz);
        // optional: if cfg implements Validatable { ((Validatable)cfg).validate(); }
        return cfg;
    }

    // convenience for deploy folder (WPILib deploy: src/main/deploy)
    public static <T> T loadFromDeploy(String filename, Class<T> clazz) throws Exception {
        String p = Filesystem.getDeployDirectory().toString() +"/" + filename; // running on laptop; on robot use Filesystem.getDeployDirectory()
        Path x = Path.of(p);
        return load(x.toFile(), clazz);
    }
}
