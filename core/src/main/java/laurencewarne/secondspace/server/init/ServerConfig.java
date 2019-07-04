package laurencewarne.secondspace.server.init;

import org.aeonbits.owner.Config;

public interface ServerConfig extends Config {
    @DefaultValue("world.json")
    String worldSaveFileLocation();
    @DefaultValue("10")
    int maxClientConnections();
    @DefaultValue("15")
    int minutesPerAutosave();
}

