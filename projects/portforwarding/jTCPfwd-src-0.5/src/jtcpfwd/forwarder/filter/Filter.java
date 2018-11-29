package jtcpfwd.forwarder.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import jtcpfwd.Module;

public abstract class Filter extends Module {

	public abstract OutputStream createFilterStream(OutputStream sameDirection, OutputStream otherDirection, Parameters parameters) throws IOException;

	public void dispose() throws IOException {
		// by default, filters do not need disposing
	}

	public static class Parameters {
		private final OutputStream sameOrig;
		private final OutputStream otherOrig;
		private final Socket srcSocket;
		private final Socket destSocket;

		public Parameters(OutputStream sameOrig, OutputStream otherOrig, Socket srcSocket, Socket destSocket) {
			this.sameOrig = sameOrig;
			this.otherOrig = otherOrig;
			this.srcSocket = srcSocket;
			this.destSocket = destSocket;
		}

		public OutputStream getSameOrig() {
			return sameOrig;
		}

		public OutputStream getOtherOrig() {
			return otherOrig;
		}

		public Socket getSrcSocket() {
			return srcSocket;
		}

		public Socket getDestSocket() {
			return destSocket;
		}
	}
}
