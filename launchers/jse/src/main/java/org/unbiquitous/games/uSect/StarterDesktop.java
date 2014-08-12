package org.unbiquitous.games.uSect;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.UUID;
import java.util.logging.Level;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.unbiquitous.games.uSect.environment.Random;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uImpala.engine.io.KeyboardManager;
import org.unbiquitous.uImpala.engine.io.MouseManager;
import org.unbiquitous.uImpala.engine.io.ScreenManager;
import org.unbiquitous.uImpala.jse.impl.core.Game;
import org.unbiquitous.uos.core.UOSLogging;

public class StarterDesktop extends StartScene {

	@SuppressWarnings({ "unchecked", "serial" })
	public static void main(String[] args) {
		if (args.length > 0 && "--debug".equalsIgnoreCase(args[0])) {
			UOSLogging.setLevel(Level.ALL);
		}
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		final int width = gd.getDisplayMode().getWidth();
		final int height = gd.getDisplayMode().getHeight() - 60;
		
		Random.setSeed(seed());
		Game.run(new GameSettings() {
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
				put("usect.player.id",UUID.randomUUID().toString());
			}
		});
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