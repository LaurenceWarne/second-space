package laurencewarne.secondspace.server.component;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;

import lombok.ToString;

@DelayedComponentRemoval @ToString
public class ComponentX extends Component {
    public String text;
}
