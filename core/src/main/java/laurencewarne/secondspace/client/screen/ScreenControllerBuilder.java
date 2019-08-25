package laurencewarne.secondspace.client.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.Array;

import lombok.NonNull;

/**
 * Class for easy construction of {@link ScreenController} objects.
 */
public class ScreenControllerBuilder {

    private Array<IProgressScreen> screens = new Array<>(4);
    private ScreenFactory finalScreenFactory = new ScreenFactory(){
	    @Override
	    public Screen create() {
		return new ScreenAdapter();  // the 'null' screen
	    }
	};

    public ScreenControllerBuilder withScreen(@NonNull IProgressScreen screen) {
	screens.add(screen);
	return this;
    }

    public ScreenControllerBuilder withFinalScreenFactory(
	@NonNull ScreenFactory screenFactory
    ) {
	finalScreenFactory = screenFactory;
	return this;
    }

    public ScreenController build() {
	return new ScreenController(finalScreenFactory, screens);
    }
}
