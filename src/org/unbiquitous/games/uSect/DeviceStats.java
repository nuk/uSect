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
	}
	
	public long cpuSpeed() {
		return cpuInfo.getMhz();
	}

	public long totalMemory() {
		return runtime.maxMemory()/1024/1024;
	}

}
