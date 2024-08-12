package jtcpfwd.util.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import jtcpfwd.util.EnglishWordCoder;
import jtcpfwd.util.PollingHandler;

public class HTTPTunnelClient {

	public static void handle(String baseURL, int timeoutValue, PollingHandler ph) throws Exception {
		handle(baseURL, timeoutValue, "create=1", ph, System.out, System.err);
	}

	public static void handle(String baseURL, int timeoutValue, String createParam, final PollingHandler ph, final PrintStream outputStream, final PrintStream errorStream) throws Exception {
		if (!createParam.startsWith("create="))
			throw new IllegalArgumentException("Invalid create parameter");
		if (baseURL.indexOf("#DATA#") != -1) {
			handleCamouflage(baseURL, timeoutValue, createParam.substring(7), ph, outputStream, errorStream);
		} else {
			handleStreaming(baseURL + "?", timeoutValue, createParam.substring(7), ph, outputStream, errorStream);
		}
	}

	private static void handleStreaming(final String baseURL, int timeoutValue, String createParam, final PollingHandler ph, final PrintStream outputStream, final PrintStream errorStream) throws Exception {
		final String timeout = timeoutValue == 0 ? "" : "&t=" + timeoutValue;
		final String[] globalID = new String[] { "create=" + createParam };

		new Thread(new HTTPReceiver(ph, timeout, globalID, baseURL, outputStream, errorStream)).start();
		synchronized (globalID) {
			while (globalID[0].equals("create=" + createParam)) {
				globalID.wait();
			}
			if (globalID[0].equals(".dead.")) {
				outputStream.println("Connection dead.");
				return;
			}
		}
		new Thread(new HTTPSender(baseURL, timeout, ph, globalID, errorStream)).start();
	}

	private static void handleCamouflage(final String template, final int timeoutValue, final String createParam, final PollingHandler ph, final PrintStream outputStream, final PrintStream errorStream) throws Exception {
		new Thread(new CamouflageHandler(createParam, errorStream, outputStream, timeoutValue, ph, template)).start();
	}

	protected static char[] generateRandom(Random rnd, int length, String chars) {
		char[] result = new char[length];
		for (int i = 0; i < result.length; i++) {
			result[i] = chars.charAt(rnd.nextInt(chars.length()));
		}
		return result;
	}

	public static final class HTTPSender implements Runnable {
		private final String baseURL;
		private final String timeout;
		private final PollingHandler ph;
		private final String[] globalID;
		private final PrintStream errorStream;

		HTTPSender(String baseURL, String timeout, PollingHandler ph, String[] globalID, PrintStream errorStream) {
			this.baseURL = baseURL;
			this.timeout = timeout;
			this.ph = ph;
			this.globalID = globalID;
			this.errorStream = errorStream;
		}

		public void run() {
			try {
				String id;
				synchronized (globalID) {
					id = globalID[0];
				}
				if (!id.startsWith("id="))
					throw new RuntimeException(id);
				id = id.substring(3);
				int counter = 0;
				boolean complete = false;
				int[] generationHolder = new int[1];
				while (true) {
					try {
						counter++;
						int off = complete ? ph.getSendOffset() : ph.getAndResetToSendAckedCount(generationHolder);
						String url = baseURL + "counter=" + counter;
						complete = false;
						HttpURLConnection sender = (HttpURLConnection) new URL(url).openConnection();
						sender.setDoOutput(true);
						boolean doStreaming = timeout.length() == 0;
						if (doStreaming) {
							try {
								sender.getClass().getMethod("setFixedLengthStreamingMode", new Class[] { int.class }).invoke(sender, new Object[] { new Integer(104857600) });
							} catch (Exception ex) {
								doStreaming = false;
							}
						}
						OutputStream sndOut = sender.getOutputStream();
						sndOut.write((id + "," + off + ";").getBytes("ISO-8859-1"));
						int sentBytes = 0;
						try {
							byte[] buf;
							while ((buf = ph.getSendBytes(15000, -1, true, generationHolder[0])) != null) {
								sndOut.write(buf, 0, buf.length);
								sentBytes += buf.length;
								sndOut.flush();
								if (!doStreaming)
									break;
							}
							// buf == null, therefore stop polling
							if (buf == null)
								break;
						} catch (IOException ex) {
							ex.printStackTrace(errorStream);
						} finally {
							try {
								sndOut.close();
							} catch (IOException ex) {
								if (!ex.getMessage().equals("insufficient data written"))
									ex.printStackTrace(errorStream);
							}
						}
						InputStream in = sender.getInputStream();
						StringBuffer sb = new StringBuffer();
						int b;
						while ((b = in.read()) != -1) {
							sb.append((char) b);
						}
						in.close();
						int value = Integer.parseInt(sb.toString());
						complete = (value == sentBytes);
						if (!complete)
							errorStream.println("Packet not complete, " + value + " != " + sentBytes);
					} catch (Exception ex) {
						ex.printStackTrace(errorStream);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace(errorStream);
			}
		}
	}

	public static final class HTTPReceiver implements Runnable {
		private final PollingHandler ph;
		private final String timeout;
		private final String[] globalID;
		private final String baseURL;
		private final PrintStream outputStream;
		private final PrintStream errorStream;

		HTTPReceiver(PollingHandler ph, String timeout, String[] globalID, String baseURL, PrintStream outputStream, PrintStream errorStream) {
			this.ph = ph;
			this.timeout = timeout;
			this.globalID = globalID;
			this.baseURL = baseURL;
			this.outputStream = outputStream;
			this.errorStream = errorStream;
		}

		public void run() {
			try {
				String id;
				synchronized (globalID) {
					id = globalID[0];
				}
				boolean firstRequest = true;
				int counter = 0, errors = 0;
				while (errors < 5) {
					counter++;
					HttpURLConnection receiver = (HttpURLConnection) new URL(baseURL + id + timeout + "&off=" + ph.getReceiveOffset() + "&counter=" + counter).openConnection();
					InputStream recIn = null;
					try {
						recIn = receiver.getInputStream();
						int b;
						if ("application/x-tunneled".equals(receiver.getContentType())) {
							while ((b = recIn.read()) != -1 && b != 0 && b != 1 && b != 0xFF) {
								outputStream.print((char) b);
							}
							if (firstRequest) {
								outputStream.println();
								if (b == 1) {
									// read id
									StringBuffer sb = new StringBuffer();
									while ((b = recIn.read()) != -1 && b != ' ') {
										sb.append((char) b);
									}
									outputStream.println("Connection established, ID=" + sb.toString());
									id = "id=" + sb.toString();
									firstRequest = false;
									synchronized (globalID) {
										globalID[0] = id;
										globalID.notifyAll();
									}
									b = recIn.read();
								} else {
									outputStream.println("No ID given");
									break;
								}
							}
							if (b == 0xff) {
								// connection closed
								return;
							}
							if (b == -1) {
								outputStream.println("Connection closed.");
								break;
							}
						} else {
							while ((b = recIn.read()) != -1) {
								outputStream.print((char) b);
							}
							outputStream.println();
							outputStream.println("Connection closed.");
							break;
						}
						byte[] buf = new byte[4096];
						int len;
						while ((len = recIn.read(buf)) != -1) {
							ph.receiveBytes(buf, 0, len);
						}
						errors = 0;
					} catch (IOException ex) {
						ex.printStackTrace(errorStream);
						errors++;
					} finally {
						if (recIn != null)
							recIn.close();
					}
				}
				synchronized (globalID) {
					globalID[0] = ".dead.";
					globalID.notifyAll();
					ph.dispose();
				}
			} catch (Exception ex) {
				ex.printStackTrace(errorStream);
			}
		}
	}

	public static final class CamouflageHandler implements Runnable {
		private final String createParam;
		private final PrintStream errorStream;
		private final PrintStream outputStream;
		private final int timeoutValue;
		private final PollingHandler ph;
		private final String template;

		CamouflageHandler(String createParam, PrintStream errorStream, PrintStream outputStream, int timeoutValue, PollingHandler ph, String template) {
			this.createParam = createParam;
			this.errorStream = errorStream;
			this.outputStream = outputStream;
			this.timeoutValue = timeoutValue;
			this.ph = ph;
			this.template = template;
		}

		public void run() {
			try {
				Random rnd = new Random();
				int id = -1;
				boolean complete = false;
				int sendTimeout = 10;
				int counter = 0;
				int[] generationHolder = new int[1];
				boolean receiveFinished = false;
				while (true) {
					counter++;
					int sendOffset = complete ? ph.getSendOffset() : ph.getAndResetToSendAckedCount(generationHolder);
					byte[] sendBytes = ph.getSendBytes(sendTimeout, 512, false, generationHolder[0]);
					if (sendBytes == null && receiveFinished)
						break;
					else if (sendBytes == null)
						sendBytes = new byte[0];

					ByteArrayOutputStream baos_ = new ByteArrayOutputStream();
					DataOutputStream rawData = new DataOutputStream(baos_);

					rawData.writeInt(id);
					if (id == -1)
						rawData.writeUTF(createParam);
					rawData.writeInt(sendOffset);
					rawData.writeInt(ph.getReceiveOffset());
					rawData.writeInt(sendBytes.length == 0 ? timeoutValue : 10);
					rawData.write(sendBytes);
					String data = new EnglishWordCoder(baos_.toByteArray()).getPlainText();
					String url = null;
					StringBuffer payload = new StringBuffer();

					int lastpos = 0, pos;
					while ((pos = template.indexOf('#', lastpos)) != -1) {
						if (pos != lastpos) {
							payload.append(template.substring(lastpos, pos));
						}
						int pos2 = template.indexOf('#', pos + 1);
						if (pos2 == -1)
							throw new RuntimeException("Missing #: " + template.substring(pos));
						String name = template.substring(pos + 1, pos2);
						if (name.equals("DATA")) {
							payload.append(data);
						} else if (name.equals("C")) {
							payload.append(counter);
						} else if (name.equals("POST")) {
							if (url != null)
								throw new RuntimeException("More than one POST marker!");
							url = payload.toString();
							payload.setLength(0);
						} else if (name.startsWith("R")) {
							payload.append(rnd.nextInt(Integer.parseInt(name.substring(1))) + 1);
						} else if (name.startsWith("D")) {
							payload.append(generateRandom(rnd, Integer.parseInt(name.substring(1)), "0123456789"));
						} else if (name.startsWith("H")) {
							payload.append(generateRandom(rnd, Integer.parseInt(name.substring(1)), "0123456789abcdef"));
						} else {
							throw new RuntimeException("Invalid template variable: " + name);
						}
						lastpos = pos2 + 1;
					}
					if (lastpos != template.length()) {
						payload.append(template.substring(lastpos));
					}
					if (url == null) {
						url = payload.toString();
						payload = null;
					}

					complete = false;
					HttpURLConnection sender = (HttpURLConnection) new URL(url).openConnection();
					if (payload != null) {
						sender.setDoOutput(true);
						OutputStream sndOut = sender.getOutputStream();
						sndOut.write(payload.toString().getBytes("ISO-8859-1"));
						sndOut.flush();
					}
					InputStream in = sender.getInputStream();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					final byte[] buf = new byte[4096];
					int length;
					while ((length = in.read(buf)) != -1) {
						baos.write(buf, 0, length);
					}
					in.close();
					String responseString = new String(baos.toByteArray(), "ISO-8859-1").replaceAll("\r\n", "\n");
					if (!responseString.startsWith("<pre>\n") || !responseString.endsWith("\n</pre>\n"))
						throw new RuntimeException("Unexpected response string: " + responseString);
					byte[] response = EnglishWordCoder.decode(responseString.substring(6, responseString.length() - 8).trim());
					DataInputStream dis = new DataInputStream(new ByteArrayInputStream(response));
					int flag = dis.readByte();
					while (flag != 1 && flag != 2) {
						outputStream.print((char) flag);
						flag = dis.readByte();
					}
					if (id == -1) {
						if (flag != 1)
							throw new RuntimeException("No ID given: " + flag);
						id = dis.readInt();
						outputStream.println("Connection established, ID=" + id);
						flag = dis.readByte();
					}
					if (flag != 2)
						throw new IOException("Invalid flag: " + flag);
					int roundtripBytes = dis.readInt();
					flag = dis.readByte();
					byte[] received = new byte[0];
					if (flag == -1) {
						receiveFinished = true;
					} else if (flag == 0) {
						int len = dis.readInt();
						received = new byte[len];
						dis.readFully(received);
					} else {
						throw new IOException("Invalid second flag: " + flag);
					}
					if (dis.readByte() != 42 || dis.read() != -1) {
						throw new IOException("Invalid end marker");
					}
					if (received.length > 0) {
						ph.receiveBytes(received, 0, received.length);
					}
					complete = (roundtripBytes == sendBytes.length);
					if (!complete)
						errorStream.println("Packet not complete, " + roundtripBytes + " != " + sendBytes.length);
					sendTimeout = received.length == 0 ? timeoutValue : 10;
				}
			} catch (Exception ex) {
				ex.printStackTrace(errorStream);
			}
		}
	}
}
