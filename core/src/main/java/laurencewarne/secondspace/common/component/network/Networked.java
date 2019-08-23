package laurencewarne.secondspace.common.component.network;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Transient @ToString
public class Networked<T extends Component> extends Component {
    @Getter @Setter
    private int id = -1;
    @Getter @Setter
    private T component;
}
