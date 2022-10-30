package com.mjp.cyberjump;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mjp.cyberjump.utils.Utils;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Cyber Jump");
		config.setWindowIcon(Utils.data + "ic_launcher.png");
		config.setWindowedMode(800, 600);
		new Lwjgl3Application(new GameHandler(), config);
	}
}
