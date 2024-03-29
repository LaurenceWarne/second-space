package laurencewarne.secondspace.common.init;

import org.aeonbits.owner.Config;

public interface ServerConfig extends Config {
    @DefaultValue("world.json")
    String worldSaveFileLocation();
    @DefaultValue("templates/")
    String templatesDirectory();
    @DefaultValue("10")
    int maxClientConnections();
    @DefaultValue("15")
    int minutesPerAutosave();
}

