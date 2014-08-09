package uPrism;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class UniqueData {

	public static void main(String[] args) throws Exception {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while(networkInterfaces.hasMoreElements()){
			NetworkInterface e = networkInterfaces.nextElement();
			System.out.print(e.isVirtual()+","+e.isLoopback()+" - ");
			if (e.getHardwareAddress() != null){
				for(byte b : e.getHardwareAddress()){
					System.out.print(b);
				}
			}
			System.out.println();
			for(InterfaceAddress ia : e.getInterfaceAddresses()){
				System.out.println("\t"+ia.getAddress());
			}
		}
	}
}
