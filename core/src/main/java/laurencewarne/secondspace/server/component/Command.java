package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Transient @Getter @Setter @ToString
public class Command extends Component {
    private String commandString;
    private boolean processed = false;
}
