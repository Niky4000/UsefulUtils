package jtcpfwd.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This class contains a utility method for matching host addresses against a
 * list of CIDR rules.
 */
public class HostMatcher {

	public static boolean isMatch(Socket s, String[] rules) throws IOException {
		return isMatch(s.getInetAddress(), rules);
	}

	public static boolean isMatch(InetAddress remoteAddress, String[] rules) throws IOException {
		for (int i = 0; i < rules.length; i++) {
			boolean isMatch;
			if (rules[i].indexOf('/') != -1) {
				int pos = rules[i].indexOf('/');
				byte[] remoteAddressBytes = remoteAddress.getAddress();
				byte[] baseAddress = InetAddress.getByName(rules[i].substring(0, pos)).getAddress();
				int routingPrefixSize = Integer.parseInt(rules[i].substring(pos + 1));
				isMatch = true;
				for (int j = 0; isMatch && j < routingPrefixSize / 8; j++) {
					if (baseAddress[j] != remoteAddressBytes[j])
						isMatch = false;
				}
				if (isMatch && routingPrefixSize % 8 != 0) {
					int idx = routingPrefixSize / 8;
					int mask = 0xFF ^ (0xFF >> (routingPrefixSize % 8));
					if ((baseAddress[idx] & mask) != (remoteAddressBytes[idx] & mask))
						isMatch = false;
				}
			} else {
				isMatch = false;
				InetAddress[] addresses = InetAddress.getAllByName(rules[i]);
				for (int j = 0; j < addresses.length; j++) {
					if (addresses[j].equals(remoteAddress))
						isMatch = true;
				}
			}
			if (isMatch)
				return true;
		}
		return false;
	}
}
