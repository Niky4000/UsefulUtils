package jtcpfwd.forwarder;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.WrappedPipedOutputStream;

public class ScreenForwarder extends Forwarder implements Runnable {

	public static final String SYNTAX = "Screen@";

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class };
	}

	public static final int HALF_KEYBOARD_BUFFER = 50;
	private static boolean started = false;
	private boolean disposed = false;

	private final Robot robot = new Robot();
	private final Rectangle screenBounds;
	private boolean active = false;
	private int sendAcked = 0;

	private InputStream in = null;
	private OutputStream out = null;

	public ScreenForwarder(String rule) throws Exception {
		Rectangle result = new Rectangle();
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for (int j = 0; j < devices.length; j++) {
			result = result.union(devices[j].getDefaultConfiguration().getBounds());
		}
		screenBounds = result;
	}

	public void dispose() throws IOException {
		disposed = true;
	}

	public Socket connect(Socket listener) {
		try {
			if (started)
				throw new IllegalStateException("Only one ScreenForwarder connection supported!");
			started = true;
			PipedSocketImpl sock = new PipedSocketImpl();
			in = sock.getLocalInputStream();
			out = sock.getLocalOutputStream();
			new Thread(this).start();
			return sock.createSocket();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void run() {
		try {
			if (in != null) {
				InputStream input = in;
				in = null;
				new Thread(this).start();
				handleInput(input);
			} else {
				handleOutput(out);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void handleInput(InputStream in) throws Exception {
		int bits = 0, bitCount = 0;
		while (!disposed) {
			int b = in.read();
			if (b == -1)
				continue;
			bits = (bits << 8) + b;
			bitCount += 8;
			synchronized (this) {
				sendAcked++;
				while (!active || sendAcked >= 2 * HALF_KEYBOARD_BUFFER)
					wait();
				while (bitCount >= 5) {
					b = bits >> (bitCount - 5);
					bits ^= b << (bitCount - 5);
					bitCount -= 5;
					pressBits(b);
				}
				if (in.available() == 0) {
					if (bitCount > 0) {
						b = bits << (5 - bitCount);
						pressBits(b);
						bits = bitCount = 0;
					}
					robot.keyPress(KeyEvent.VK_X);
					robot.keyRelease(KeyEvent.VK_X);
				}
			}
		}
	}

	private void pressBits(int b) {
		if (b >= 32 || b < 0)
			throw new IllegalArgumentException("" + b);
		if (b < 10)
			b += KeyEvent.VK_0;
		else
			b += KeyEvent.VK_A - 10;
		robot.keyPress(b);
		robot.keyRelease(b);
	}

	private static final int COLOR_RED = Color.RED.getRGB(), COLOR_GREEN = Color.GREEN.getRGB(), COLOR_BLUE = Color.BLUE.getRGB(), COLOR_YELLOW = Color.YELLOW.getRGB(), COLOR_BLACK = Color.BLACK.getRGB(), COLOR_WHITE = Color.WHITE.getRGB();

	private void handleOutput(OutputStream out) throws Exception {
		boolean active = false; // kept in sync with the global flag
		int baseX = 0, baseY = 0, scanX = 0, scanY = 0;
		while (!disposed) {
			if (!active) {
				BufferedImage bufImg;
				synchronized (this) {
					bufImg = robot.createScreenCapture(screenBounds);
				}
				for (int x = 1; x + 602 < bufImg.getWidth(); x++) {
					for (int y = 1; y + 402 < bufImg.getHeight(); y++) {
						if (checkCorner(bufImg, x, y, -1, -1, COLOR_RED)) {
							if (checkCorner(bufImg, x + 601, y, 1, -1, COLOR_GREEN) && checkCorner(bufImg, x, y + 401, -1, 1, COLOR_BLUE) && checkCorner(bufImg, x + 601, y + 401, 1, 1, COLOR_WHITE)) {
								baseX = x + 1;
								baseY = y + 1;
								scanX = scanY = 0;
								synchronized (this) {
									active = true;
									robot.keyPress(KeyEvent.VK_X);
									robot.keyRelease(KeyEvent.VK_X);
									this.active = true;
									notifyAll();
								}
								break;
							}
						}
					}
				}
				if (!active) {
					Thread.sleep(1000);
				}
			} else {
				BufferedImage bufImg;
				synchronized (this) {
					bufImg = robot.createScreenCapture(new Rectangle(baseX, baseY, 600, 400));
				}
				int parsed = 0;
				int col;
				while ((col = bufImg.getRGB(scanX + 3, scanY)) != COLOR_BLACK) {
					if (col == COLOR_YELLOW) {
						synchronized (this) {
							if (bufImg.getRGB(scanX, scanY) == COLOR_YELLOW && bufImg.getRGB(scanX + 1, scanY) == COLOR_RED && bufImg.getRGB(scanX + 2, scanY) == COLOR_RED) {
								if (sendAcked < HALF_KEYBOARD_BUFFER)
									throw new IllegalStateException("" + sendAcked);
								sendAcked -= HALF_KEYBOARD_BUFFER;
								notifyAll();
							} else if (bufImg.getRGB(scanX, scanY) == COLOR_YELLOW && bufImg.getRGB(scanX + 1, scanY) == COLOR_YELLOW && bufImg.getRGB(scanX + 2, scanY) == COLOR_YELLOW) {
								robot.keyPress(KeyEvent.VK_W);
								robot.keyRelease(KeyEvent.VK_W);
								robot.keyPress(KeyEvent.VK_X);
								robot.keyRelease(KeyEvent.VK_X);
								active = false;
								this.active = false;
								break;
							} else {
								throw new IllegalStateException();
							}
						}
					} else {
						int b = 0;
						for (int i = 0; i < 4; i++) {
							if (i != 0)
								col = bufImg.getRGB(scanX + 3 - i, scanY);
							int newBits = decodeColor(col);
							if (newBits == -1)
								throw new IllegalStateException("" + col);
							b = (b << 2) | newBits;
						}
						out.write(b);
					}
					parsed++;
					scanX += 4;
					if (scanX == 600) {
						scanY = (scanY + 1) % 400;
						scanX = 0;
					}
				}
				if (active && parsed == 0) {
					out.flush();
					Thread.sleep(100);
				} else if (active && parsed > 0) {
					synchronized (this) {
						robot.keyPress(KeyEvent.VK_W);
						robot.keyRelease(KeyEvent.VK_W);
						sendAck(parsed, false);
					}
				}
			}
		}
	}

	private int decodeColor(int col) {
		if (col == COLOR_RED)
			return 0;
		if (col == COLOR_GREEN)
			return 1;
		if (col == COLOR_BLUE)
			return 2;
		if (col == COLOR_WHITE)
			return 3;
		return -1;
	}

	private void sendAck(int toAck, boolean shifted) {
		if (toAck >= 16) {
			sendAck(toAck / 16, true);
			sendAck(toAck % 16, shifted);
			return;
		}
		if (shifted) {
			toAck += 16;
		}
		pressBits(toAck);
	}

	private boolean checkCorner(BufferedImage bufImg, int x, int y, int dx, int dy, int centerColor) {
		if (bufImg.getRGB(x, y) != centerColor)
			return false;
		if (bufImg.getRGB(x + dx, y - dy) != COLOR_YELLOW)
			return false;
		if (bufImg.getRGB(x + dx, y) != COLOR_YELLOW)
			return false;
		if (bufImg.getRGB(x + dx, y + dy) != COLOR_YELLOW)
			return false;
		if (bufImg.getRGB(x, y + dy) != COLOR_YELLOW)
			return false;
		if (bufImg.getRGB(x - dx, y + dy) != COLOR_YELLOW)
			return false;
		if (bufImg.getRGB(x - dx, y) != COLOR_BLACK)
			return false;
		if (bufImg.getRGB(x - dx, y - dy) != COLOR_BLACK)
			return false;
		if (bufImg.getRGB(x, y - dy) != COLOR_BLACK)
			return false;
		return true;
	}
}
