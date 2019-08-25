package laurencewarne.secondspace.client;

import com.badlogic.gdx.Screen;

import lombok.Getter;
import lombok.NonNull;

public class ScreenController {
    @Getter
    private Screen activeScreen;

    private LoadingScreen loadingScreen;
    private Screen mainMenuScreen;
    private ConnectionScreen connectionScreen;
    private GameScreen gameScreen;

    public ScreenController(
	@NonNull LoadingScreen loadingScreen,
	@NonNull ConnectionScreen connectionScreen,
	@NonNull GameScreen gameScreen
	
    ) {
	this.loadingScreen = loadingScreen;
	this.connectionScreen = connectionScreen;
	this.gameScreen = gameScreen;

	activeScreen = loadingScreen;
    }

    public boolean checkForScreenChange() {
	boolean changeMade = false;
	if (activeScreen == loadingScreen && !loadingScreen.isLoadingComplete()){
	    activeScreen = connectionScreen;
	    changeMade = true;
	}
	else if (activeScreen == connectionScreen
		 && connectionScreen.isConnected()){
	    activeScreen = gameScreen;
	    changeMade = true;
	}
	return changeMade;
    }

    public void dipose() {
	loadingScreen.dispose();
	connectionScreen.dispose();
	gameScreen.dispose();
    }
}
