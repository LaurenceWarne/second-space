package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import laurencewarne.secondspace.client.SecondSpaceClient;
import laurencewarne.secondspace.server.SecondSpaceServerBase;
import laurencewarne.secondspace.server.SecondSpaceServerUI;

public class DesktopLauncher {
    public static void main (String[] args) {
	LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
	// Run client
	if (args.length > 0 && "--client".equals(args[0])) {
	    new LwjglApplication(new SecondSpaceClient(), config);
	}
	// Run server
	else {
	    // See if we should run a headless server
	    if (args.length > 1 && "--headless".equals(args[1])) {
		HeadlessApplicationConfiguration hConfig = new HeadlessApplicationConfiguration();
		new HeadlessApplication(new SecondSpaceServerBase(), hConfig);
	    }
	    // Else run debug server
	    else {
		new LwjglApplication(new SecondSpaceServerUI(), config);
	    }
	}
	
    }
}
