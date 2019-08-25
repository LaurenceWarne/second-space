package laurencewarne.secondspace.client;

/**
 * Maps server ids to client ids.
 */
public interface IdTranslator {

    /**
     * Get the client id of the specified server entity.
     * 
     * @param id of server entity
     * @return id of client entity
     */
    int translate(int id);

    /**
     * Remove the specified server id from this translator.
     */
    void remove(int id);
}
