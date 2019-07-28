package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Transient @Getter @Setter @ToString
public class SpawnRequest extends Component {
    private String templateName = "";
    private float x = 0f;
    private float y = 0f;
}
