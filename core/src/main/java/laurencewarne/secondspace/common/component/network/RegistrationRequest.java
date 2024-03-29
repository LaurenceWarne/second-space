package laurencewarne.secondspace.common.component.network;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=false) @Transient
public class RegistrationRequest extends Component {
    private String name = "player";
}
