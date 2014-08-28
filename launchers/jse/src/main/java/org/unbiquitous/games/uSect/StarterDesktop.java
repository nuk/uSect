package org.unbiquitous.games.uSect;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.unbiquitous.games.uSect.environment.Random;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.engine.io.KeyboardManager;
import org.unbiquitous.uImpala.engine.io.MouseManager;
import org.unbiquitous.uImpala.engine.io.ScreenManager;
import org.unbiquitous.uImpala.jse.impl.core.Game;
import org.unbiquitous.uos.core.UOSLogging;

public class StarterDesktop extends StartScene {

	static File scriptsFolder = new File("scripts/");
	
	@SuppressWarnings({ "unchecked", "serial" })
	public static void main(String[] _args) {
		List<String> args = Arrays.asList(_args);
		if (args.contains("--debug")) {
			UOSLogging.setLevel(Level.ALL);
		}
		if (args.contains("--sects")) {
			int folderIndex = args.indexOf("--sects") + 1;
			scriptsFolder = new File(args.get(folderIndex));
		}
		
		GameComponents.get(null);
		
		Random.setSeed(seed());
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		final int width = gd.getDisplayMode().getWidth();
		final int height = gd.getDisplayMode().getHeight() - 60;
		
		GameSettings settings = new GameSettings() {
			{
				put("first_scene", StartScene.class);
				put("game_id", "uSect");
				put("input_managers", Arrays.asList(MouseManager.class,
						KeyboardManager.class));
				put("output_managers", Arrays.asList(ScreenManager.class));
				put("usect.speed.value", 5);
				put("usect.width", width);
				put("usect.height", height);
				put("usect.devicestats", new DeviceStatsJSE());
//				put("usect.player.id",UUID.randomUUID().toString());
			}
		};
		
		if(scriptsFolder.exists() && scriptsFolder.isDirectory()){
			List<String> scripts = new ArrayList<String>();
			for(File f : scriptsFolder.listFiles()){
				if(f.getName().endsWith(".lua")){
					try {
						BufferedReader r = new BufferedReader(new FileReader(f));
						StringBuilder content = new StringBuilder();
						while(r.ready()){
							content.append(r.readLine());
							content.append('\n');
						}
						scripts.add(content.toString());
						r.close();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
			settings.put("usect.artificials", scripts);
		}
		
		Game.run(settings);
	}

	private static long seed() {
		try {
			long seed = 0;
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface e = networkInterfaces.nextElement();
				if (!e.isVirtual() && !e.isLoopback()) {
					if (e.getHardwareAddress() != null) {
						for (byte b : e.getHardwareAddress()) {
							seed += b;
						}
					}
				}
			}
			return seed;
		} catch (SocketException e) {
			return (long) (Math.random() * Long.MAX_VALUE);
		}
	}

}

class DeviceStatsJSE extends DeviceStats {
	private CpuInfo cpuInfo;
	private Runtime runtime;

	public DeviceStatsJSE() {
		try {
			Sigar s = new Sigar();
			cpuInfo = s.getCpuInfoList()[0];
			runtime = Runtime.getRuntime();
		} catch (SigarException e) {
			throw new RuntimeException(e);
		}
	}

	public long totalMemory() {
		return cpuInfo.getMhz();
	}

	public long cpuSpeed() {
		return runtime.maxMemory() / 1024 / 1024;
	}
}