package laurencewarne.secondspace.common.component.network;

import com.artemis.Component;
import com.artemis.annotations.Transient;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Transient @ToString
public class Networked<T extends Component> extends Component {
    @Getter @Setter
    private int id = -1;
    @Getter @Setter
    private Class<T> type;
    @Getter
    private T component;

    public void setComponent(@NonNull T component) {
	this.component = component;
	try {
	    this.type = (Class<T>) component.getClass();
	} catch (ClassCastException e) {}
    }
}
