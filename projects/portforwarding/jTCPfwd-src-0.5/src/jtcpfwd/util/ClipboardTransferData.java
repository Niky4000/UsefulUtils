package jtcpfwd.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class ClipboardTransferData implements Serializable, ClipboardOwner, Transferable {

	private int listenerID;
	private int senderID;
	private int sequenceNumber;
	private byte[] data;

	private static transient final DataFlavor flavor = new DataFlavor(ClipboardTransferData.class, "jTCPfwd Data Transfer Container");
	private static transient final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	private transient boolean waiting = false, disposed = false;

	public ClipboardTransferData() {
		listenerID = (int) (Math.random() * Integer.MAX_VALUE);
		senderID = -1;
		sequenceNumber = -1;
	}

	public void connect() {
		if (senderID != -1 || sequenceNumber != -1)
			throw new IllegalStateException();
		senderID = (int) (Math.random() * Integer.MAX_VALUE);
		sequenceNumber = 0;
	}

	public void interact(OutputStream out, InputStream in, boolean fromListener) throws IOException {
		sequenceNumber++;
		if (sequenceNumber % 2 != (fromListener ? 1 : 0))
			throw new IllegalStateException();
		if (data != null)
			out.write(data);
		if (in.available() > 0) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[4096];
			while (in.available() > 0) {
				int len = in.read(buf);
				if (len == -1)
					throw new EOFException();
				baos.write(buf, 0, len);
			}
			data = baos.toByteArray();
		} else {
			data = null;
		}
	}

	public synchronized ClipboardTransferData copyAndWait() throws IOException {
		waiting = true;
		try {
			while (true) {
				try {
					if (disposed) return null;
					clipboard.setContents(this, this);
					break;
				} catch (IllegalStateException ex) {
					System.err.println(ex.toString());
					Thread.sleep(1000);
				}
			}
			while (waiting) {
				if (disposed) return null;
				wait();
			}
			ClipboardTransferData newData = fromClipboard();
			if (newData.sequenceNumber != sequenceNumber + 1)
				throw new IOException("Invalid sequence number");
			if (newData.listenerID != listenerID)
				throw new IOException("Invalid listener ID");
			if (newData.senderID != senderID && !(senderID == -1 && sequenceNumber == -1))
				throw new IOException("Invalid listener ID");
			return newData;

		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	public synchronized void lostOwnership(Clipboard clipboard, Transferable contents) {
		waiting = false;
		notifyAll();
	}

	public boolean isDataFlavorSupported(DataFlavor f) {
		return flavor.equals(f);
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { flavor };
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return this;
	}

	public static ClipboardTransferData fromClipboard() throws InterruptedException, IOException {
		while (true) {
			try {
				return (ClipboardTransferData) clipboard.getContents(null).getTransferData(flavor);
			} catch (IllegalStateException ex) {
				Thread.sleep(1000);
			} catch (UnsupportedFlavorException ex) {
				IOException ex2 = new IOException("Unsupported data in clipboard");
				ex2.initCause(ex);
				throw ex2;
			}
		}
	}
	
	public synchronized void dispose() {
		disposed = true;
		notifyAll();
	}
}
