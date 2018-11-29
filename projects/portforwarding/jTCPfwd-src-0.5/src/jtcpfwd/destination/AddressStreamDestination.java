package jtcpfwd.destination;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.forwarder.Forwarder;
import jtcpfwd.listener.Listener;

public class AddressStreamDestination extends Destination {

	public static final String SYNTAX = ":AddressStream:<listener>";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Listener.class, Forwarder.class,
				NoMoreSocketsException.class };
	}

	private ObjectInputStream in;
	private final Listener listener;

	public AddressStreamDestination(String rule) throws Exception {
		listener = Lookup.lookupListener(rule);
	}

	public Module[] getUsedModules() {
		return new Module[] { listener };
	}

	public InetSocketAddress getNextDestination() {
		try {
			if (in == null) {
				in = new ObjectInputStream(listener.accept().getInputStream());
				listener.dispose();
			}
			synchronized (in) {
				return (InetSocketAddress) in.readObject();
			}
		} catch (NoMoreSocketsException ex) {
			// ignore
			return null;
		} catch (ConnectException ex) {
			// ignore
			return null;
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			return null;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void dispose() throws IOException {
		if (in != null) {
			in.close();
		} else {
			listener.dispose();
		}
	}
}
