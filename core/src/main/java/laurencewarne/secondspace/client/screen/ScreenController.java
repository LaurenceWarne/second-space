package laurencewarne.secondspace.client.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;

import lombok.Getter;
import lombok.NonNull;

public class ScreenController {
    @Getter
    private Screen activeScreen;
    @NonNull
    private Array<IProgressScreen> screens;
    private int screensIndex;
    @NonNull
    private ScreenFactory finalScreenFactory;
    private Screen finalScreen;

    public ScreenController(
	@NonNull ScreenFactory finalScreenFactory,
	@NonNull Array<IProgressScreen> screens
    ) {
	this.screens = screens;
	this.finalScreenFactory = finalScreenFactory;
	if (this.screens.isEmpty()) {
	    this.screensIndex = -1;
	    this.activeScreen = finalScreen;
	}
	else {
	    this.screensIndex = 0;
	    this.activeScreen = this.screens.get(screensIndex);
	}
    }

    public boolean checkForScreenChange() {
	boolean change = false;
	if (screensIndex != -1) {
	    final IProgressScreen screen = screens.get(screensIndex);
	    if (screen.isFinished() && screensIndex < screens.size - 1) {
		activeScreen = screens.get(++screensIndex);
		change = true;
	    }
	    else if (screen.isFinished() && screensIndex == screens.size - 1) {
		activeScreen = finalScreen = finalScreenFactory.create();
		screensIndex = -1;
		change = true;
	    }
	}
	return change;
    }

    public void dipose() {
	for (Screen screen : screens) {
	    screen.dispose();
	}
	finalScreen.dispose();
    }
}
