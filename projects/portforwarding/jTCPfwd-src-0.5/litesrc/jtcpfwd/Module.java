package jtcpfwd;

import java.io.IOException;
import java.net.Socket;

/**
 * A module that is dynamically loaded. Module types are
 * {@link jtcpfwd.forwarder.Forwarder}, {@link jtcpfwd.listener.Listener}, or
 * {@link jtcpfwd.destination.Destination}
 */
public abstract class Module {

	public static Class lookup(Class baseClass, String moduleName) throws ClassNotFoundException {
		String className = baseClass.getName();
		int pos = className.lastIndexOf('.');
		className = className.substring(0, pos + 1) + moduleName + className.substring(pos + 1);
		return Class.forName(className);
	}

	/**
	 * Get a list of modules that is used internally in this module. For example
	 * a {@link jtcpfwd.forwarder.MuxForwarder} internally forwards to another
	 * forwarder, which will appear in this list. Used to dynamically find all
	 * classes needed for a given listener/forwarder command line.
	 */
	public Module[] getUsedModules() {
		return new Module[0];
	}

	/**
	 * Release all the resources (like {@link Socket}s) held by this module.
	 */
	public abstract void dispose() throws IOException;
}
