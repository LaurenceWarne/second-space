package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import laurencewarne.secondspace.client.SecondSpaceClient;
import laurencewarne.secondspace.server.SecondSpaceServer;

public class DesktopLauncher {
    public static void main (String[] args) {
	LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
	if (args.length > 0 && "--client".equals(args[0])) {
	    // run client
	    new LwjglApplication(new SecondSpaceClient(), config);
	}
	else {
	    // run server
	    new LwjglApplication(new SecondSpaceServer(), config);
	}
	
    }
}
