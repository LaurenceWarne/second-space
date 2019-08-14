package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Signals entity can perform motion.
 */
@Data @EqualsAndHashCode(callSuper=false)
public class Thruster extends Component {
    private float power = 1f;
    private float localApplX = 0f;
    private float localApplY = 0f;

    /**
     * Signals thruster has been activated
     */
    @Transient
    public static class ThrusterActivated extends Component {
	
    }
}
