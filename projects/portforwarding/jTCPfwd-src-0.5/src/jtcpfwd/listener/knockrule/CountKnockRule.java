package jtcpfwd.listener.knockrule;

import java.io.IOException;
import java.net.InetAddress;

public class CountKnockRule extends KnockRule implements Runnable {

	public static final String SYNTAX = "Count#<count>[#<delay>[#<burstcount>]]";

	private final int count, delay, burstcount;

	public CountKnockRule(String[] args) throws Exception {
		count = Integer.parseInt(args[0]);
		delay = (args.length > 1) ? Integer.parseInt(args[1]) : 0;
		burstcount = (args.length > 2) ? Integer.parseInt(args[2]) : 1;
		if (args.length > 3)
			throw new IllegalArgumentException("Invalid number of parameters");
	}

	public void listen() throws IOException {
		new Thread(this).start();
	}

	public void tryDispose() throws IOException {
	}

	public void run() {
		for (int i = 0; i < count; i++) {
			if (i % burstcount == 0) {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			addAddress(null);
		}
	}

	public void trigger(InetAddress peer) throws IOException {
		// nothing to do
	}
}
