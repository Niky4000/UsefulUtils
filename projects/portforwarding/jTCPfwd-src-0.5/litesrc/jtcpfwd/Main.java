/* 
 * jTCPfwd - simple tcp forwarder/proxy
 * 
 * Copyright (c) 2006-2010 Michael Schierl
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *   
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *   
 * - Neither name of the copyright holders nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND THE CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jtcpfwd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jtcpfwd.destination.Destination;
import jtcpfwd.forwarder.Forwarder;
import jtcpfwd.listener.Listener;
import jtcpfwd.util.StreamForwarder;

/**
 * Main class, parsing arguments and config files.
 */
public class Main {

	public static final String[] SUPPORTED_DESTINATIONS = new String[] {
			"Simple", "RoundRobin", "AddressStream"
	};

	public static final String[] SUPPORTED_LISTENERS = new String[] {
			"Simple", "Reverse", "Forwarder", "PeerFilter",
			"SSL", "Coupler", // end of lite
			"Combine", "UDPTunnel", "UDPTunnelPTP", "UDP",
			"SOCKS", "SOCKSProxy", "Restartable",
			"File", "Clipboard", "Screen", "HTTPTunnel", "Internal",
			"Watchdog",
			"Knock" // last one!
	};

	public static final String[] SUPPORTED_FORWARDERS = new String[] {
			"Simple", "Reverse", "ListenOnce", "Retry", "Mux",
			"DeMux", "PeerDeMux", "RoundRobin", "SSL", // end of lite
			"Split", "Combine", "UDPTunnel", "UDPTunnelPTP", "UDP",
			"SOCKS", "Flaky", "Restartable", "Blackhole",
			"File", "Clipboard", "Screen", "HTTPTunnel", "Internal",
			"Watchdog", "Console", "StdInOut", "Knock",
			"Filter" // last one!
	};

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, ForwarderThread.class,
				ForwarderHandlerThread.class, StreamForwarder.class,
				Listener.class, Forwarder.class, Destination.class,
				NoMoreSocketsException.class };
	}

	public static final String VERSION = "0.5";

	/**
	 * Entry point of this application.
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("JTCPfwd " + VERSION);
		System.out.println();
		start(args);
	}

	public static ForwarderThread[] start(String[] args) throws Exception {
		ForwarderThread[] result;
		if (args.length == 1 && args[0].startsWith("@")) {
			List/* <ForwarderThread> */threads = new ArrayList();
			BufferedReader br = new BufferedReader(new FileReader(args[0].substring(1)));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0 || line.startsWith("#"))
					continue;
				String[] parts = line.split("[ \t]+");
				if (parts.length == 2) {
					ForwarderThread t = new ForwarderThread(parts[0], parts[1]);
					threads.add(t);
					t.start();
				} else {
					throw new IOException("Invalid line in config file: " + line);
				}
			}
			result = (ForwarderThread[]) threads.toArray(new ForwarderThread[threads.size()]);
		} else if (args.length == 3 && args[0].equals("-listdestination")) {
			int count = Integer.parseInt(args[1]);
			Destination dest = Destination.lookupDestination(args[2]);
			for (int i = 0; i < count; i++) {
				System.out.println(dest.getNextDestination());
			}
			dest.dispose();
			return new ForwarderThread[0];
		} else if (args.length == 0 || args.length % 2 != 0) {
			System.out.println("Usage: java -jar jTCPfwd.jar @<configfile>");
			System.out.println("       java -jar jTCPfwd.jar <listener> <forwarder> [<lis2> <forw2> [...]]]");
			System.out.println("       java -jar jTCPfwd.jar -listdestination <count> <destination>");
			System.out.println();
			System.out.println("<configfile> is a file which contains one proxy rule (<listener> <forwarder>)");
			System.out.println("on each line.");
			System.out.println("Lines starting with # are ignored as comments. Alternatively, proxy rules can");
			System.out.println("be given on the command line.");
			System.out.println();
			System.out.println("Both <listener> and <forwarder> can start with  '<type>@', giving the type of");
			System.out.println("the listener or forwarder. Types are case sensitive.");
			System.out.println();
			System.out.println("-listdestination prints the first <count> results produced by <destination>.");
			System.out.println();
			System.out.println("Destinations can be given as <host>:<port>, or in more sophisticated formats:");
			printSyntax(Destination.class, SUPPORTED_DESTINATIONS);
			System.out.println();
			System.out.println("Supported listener types:");
			printSyntax(Listener.class, SUPPORTED_LISTENERS);
			System.out.println();
			System.out.println("Supported forwarder types:");
			printSyntax(Forwarder.class, SUPPORTED_FORWARDERS);
			return new ForwarderThread[0];
		} else {
			result = new ForwarderThread[args.length / 2];
			for (int i = 0; i < args.length; i += 2) {
				result[i / 2] = new ForwarderThread(args[i], args[i + 1]);
				result[i / 2].start();
			}
		}
		System.out.println("All forwarders started.");
		return result;
	}

	private static void printSyntax(Class baseClass, String[] names) throws Exception {
		for (int i = 0; i < names.length; i++) {
			try {
				Class clazz = Module.lookup(baseClass, names[i]);
				System.out.println("  " + (String) clazz.getField("SYNTAX").get(null));
			} catch (ClassNotFoundException ex) {
				// ignore
			}
		}
	}
}