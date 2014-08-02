package org.unbiquitous.games.uSect;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class DeviceStats {

	private CpuInfo cpuInfo;
	private Runtime runtime;

	public DeviceStats() {
		try {
			Sigar s = new Sigar();
			cpuInfo = s.getCpuInfoList()[0];
			runtime = Runtime.getRuntime();
		} catch (SigarException e) {
			throw new RuntimeException(e);
		}
		//TODO:fix this
	}
	
	public long cpuSpeed() {
		return cpuInfo.getMhz();
//		return 1000;
	}

	public long totalMemory() {
		return runtime.maxMemory()/1024/1024;
//		return 2000;
	}

}
