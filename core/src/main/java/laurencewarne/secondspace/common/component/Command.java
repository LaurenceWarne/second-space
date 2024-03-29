package laurencewarne.secondspace.common.component;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Encapsulates a command.
 */
@Transient @Getter @Setter @ToString
public class Command extends Component {
    private String commandString = "nop";
}
