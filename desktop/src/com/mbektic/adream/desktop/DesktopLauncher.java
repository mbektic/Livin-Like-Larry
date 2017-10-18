package com.mbektic.adream.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mbektic.adream.ADream;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = ADream.width;
		config.height = ADream.height;
		config.title = ADream.title;
		new LwjglApplication(new ADream(), config);
	}
}
