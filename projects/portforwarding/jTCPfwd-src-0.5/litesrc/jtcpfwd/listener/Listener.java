package jtcpfwd.listener;

import java.io.IOException;
import java.net.Socket;

import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;

public abstract class Listener extends Module {

	protected boolean disposed;

	public final Socket accept() throws NoMoreSocketsException {
		while (true) {
			try {
				Socket ss = tryAccept();
				if (ss != null)
					return ss;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			if (disposed)
				throw new NoMoreSocketsException();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException ex) {
			}
		}
	}

	public void dispose() throws IOException {
		tryDispose();
		disposed = true;
	}

	protected abstract Socket tryAccept() throws IOException, NoMoreSocketsException;

	protected abstract void tryDispose() throws IOException;
}
