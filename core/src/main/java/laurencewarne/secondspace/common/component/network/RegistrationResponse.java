package laurencewarne.secondspace.common.component.network;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.Setter;

@Transient @Getter @Setter
public class RegistrationResponse extends Component {
    private int playerId = -1;
}
