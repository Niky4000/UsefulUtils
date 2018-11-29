package jtcpfwd.forwarder;

import java.net.Socket;

import jtcpfwd.Module;

/**
 * A destination where data can be forwarded to.
 */
public abstract class Forwarder extends Module {

	/**
	 * Connect a new socket where the listener will be forwarded to.
	 */
	public abstract Socket connect(Socket listener);
}
