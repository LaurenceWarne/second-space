package laurencewarne.secondspace.common.component.network;

import com.artemis.Component;
import com.artemis.annotations.Transient;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import lombok.Getter;

@Transient
public class Networked extends Component {
    @Getter
    private int id = -1;
    @Getter
    private final ClassToInstanceMap<Component> numberDefaults =
	MutableClassToInstanceMap.create();
}
