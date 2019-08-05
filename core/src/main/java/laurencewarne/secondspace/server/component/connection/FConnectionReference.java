package laurencewarne.secondspace.server.component.connection;

import com.artemis.Component;
import com.artemis.annotations.Transient;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import lombok.ToString;
import lombok.experimental.Delegate;

@ToString @Transient
public class FConnectionReference extends Component {
    @Delegate
    private BiMap<Integer, Integer> entityToLinkMap = HashBiMap.create(4);
}

