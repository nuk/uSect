package uPrism;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Sigar;


public class SystemProperties {
	public static void main(String[] args) throws Exception {
		System.out.println(Runtime.getRuntime().availableProcessors());
		System.out.println(Runtime.getRuntime().maxMemory()/1024/1024);
		System.out.println(getInfo());
		
		Sigar s = new Sigar();
		System.out.println(s.getCpu().getIdle());
		CpuInfo cpuInfo = s.getCpuInfoList()[0];
		System.out.println(cpuInfo.getVendor());
		System.out.println(cpuInfo.getMhz());
	}
	
	private static String getInfo() {
        StringBuffer sb = new StringBuffer();
//        sb.append("abi: ").append(Build.CPU_ABI).append("\n");
        if (new File("/proc/cpuinfo").exists()) {
            try {
                BufferedReader br = new BufferedReader(
                    new FileReader(new File("/proc/cpuinfo")));
                String aLine;
                while ((aLine = br.readLine()) != null) {
                    sb.append(aLine + "\n");
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }
        return sb.toString();
    }
}
