package laurencewarne.secondspace.common.component;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Transient @Getter @Setter @ToString
public class EntityTemplate extends Component {
    private String name = "";
    private byte[] bytes;
}
