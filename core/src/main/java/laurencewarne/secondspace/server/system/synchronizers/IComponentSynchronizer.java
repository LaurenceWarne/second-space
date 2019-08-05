package laurencewarne.secondspace.server.system.synchronizers;

import com.artemis.Component;

public interface IComponentSynchronizer<F extends Component, B extends Component> {

    void onFrontEndChanged(int id);

    void onFrontEndRemoved(int id);
}
