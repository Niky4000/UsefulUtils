package jtcpfwd.listener.knockrule;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jtcpfwd.Module;

public abstract class KnockRule extends Module {

	public static KnockRule lookup(StringTokenizer st) throws Exception {
		String name = st.nextToken();
		String[] args = new String[st.countTokens()];
		for (int i = 0; i < args.length; i++) {
			args[i] = st.nextToken();
		}
		Class c = Module.lookup(KnockRule.class, name);
		return (KnockRule) c.getConstructor(new Class[] { String[].class }).newInstance(new Object[] { args });
	}

	private final List addresses = new ArrayList();
	protected boolean disposed = false;

	/**
	 * Start listening for knocks. Should <b>not</b> block!
	 */
	public abstract void listen() throws IOException;

	/**
	 * Trigger the knock rule at the given peer.
	 */
	public abstract void trigger(InetAddress peer) throws IOException;

	/**
	 * Add an address to the rule's internal list of allowed addresses.
	 */
	protected synchronized void addAddress(InetAddress nextAddress) {
		addresses.add(nextAddress);
		notifyAll();
	}

	/**
	 * Return the next InetAddress which is allowed to create a connection, or
	 * <code>null</code> if every address is allowed.
	 */
	public synchronized InetAddress getNextAddress() throws InterruptedException {
		while (addresses.size() == 0) {
			if (disposed)
				throw new InterruptedException("Knock rule disposed");
			wait();
		}
		return (InetAddress) addresses.remove(0);
	}
	
	public final void dispose() throws IOException {
		synchronized(this) {
			disposed = true;
			notifyAll();
		}
		tryDispose();
	}
	
	protected abstract void tryDispose() throws IOException;
}
