package laurencewarne.secondspace.server.component;

import java.io.InputStream;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.Setter;

@Transient @Getter @Setter
public class EntityTemplate extends Component {
    private String name = "";
    private InputStream stream;
}
