package com.gikdew.shapes.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gikdew.shapes.ShapesGame;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Shapes";
		config.width = 320;
		config.height = 480;
		new LwjglApplication(new ShapesGame(new ActionResolverDesktop()),
				config);
	}
}
