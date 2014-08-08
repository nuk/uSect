package org.unbiquitous.games.uSect;


public abstract class DeviceStats {

//	private CpuInfo cpuInfo;
//	private Runtime runtime;

	public DeviceStats() {
		/*try {
			Sigar s = new Sigar();
			cpuInfo = s.getCpuInfoList()[0];
			runtime = Runtime.getRuntime();
		} catch (SigarException e) {
			throw new RuntimeException(e);
		}*/
		//TODO:fix this
	}
	
	public abstract long cpuSpeed();
//	{
////		return cpuInfo.getMhz();
//		return 1000;
//	}

	public abstract long totalMemory();
//	{
////		return runtime.maxMemory()/1024/1024;
//		return 2000;
//	}

}
