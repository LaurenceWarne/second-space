package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.Setter;

@Transient @Getter @Setter
public class Command extends Component {

    private String commandString;
}
