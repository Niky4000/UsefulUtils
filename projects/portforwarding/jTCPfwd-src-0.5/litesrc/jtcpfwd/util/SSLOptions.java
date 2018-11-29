package jtcpfwd.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLOptions {

	private static final int CLIENT_CERT_WANTED = 1;
	private static final int CLIENT_CERT_NEEDED = 2;
	private final SSLContext ctx;
	private final String rule;
	private final boolean serverMode;
	private final int clientCert;

	private SSLSocketFactory ssf = null;
	private SSLServerSocketFactory sssf = null;
	private final String[] ciphers;
	private final String[] protocols;

	private SSLOptions(SSLContext ctx, String rule, boolean serverMode, int clientCert, String[] ciphers, String[] protocols) {
		this.ctx = ctx;
		this.rule = rule;
		this.serverMode = serverMode;
		this.clientCert = clientCert;
		this.ciphers = ciphers;
		this.protocols = protocols;
	}

	public SSLContext getContext() {
		return ctx;
	}

	public String getRule() {
		return rule;
	}

	public static SSLOptions parseOptions(String rule, boolean listener) throws Exception {
		StringTokenizer st = new StringTokenizer(rule.substring(1), "" + rule.charAt(0));
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init((KeyStore) null);
		TrustManager[] tms = tmf.getTrustManagers();
		String keyStoreFile = new File(System.getProperty("user.home"), ".keystore").getAbsolutePath();
		String[] keyOptions = null;
		String ciphers = null, protocols = null;
		int clientCert = 0;
		while (st.hasMoreTokens()) {
			String option = st.nextToken();
			if (!st.hasMoreTokens()) {
				rule = option;
				break;
			}
			int pos = option.indexOf("=");
			if (pos == -1)
				throw new IllegalArgumentException("Invalid SSL option: " + option);
			String key = option.substring(0, pos);
			String value = option.substring(pos + 1);
			if (key.equals("mode")) {
				if (value.equals("client"))
					listener = false;
				else if (value.equals("server"))
					listener = true;
				else
					throw new IllegalArgumentException("Invalid option: mode=" + value);
			} else if (key.equals("clientcert")) {
				if (value.equals("wanted"))
					clientCert = CLIENT_CERT_WANTED;
				else if (value.equals("needed"))
					clientCert = CLIENT_CERT_NEEDED;
				else
					throw new IllegalArgumentException("Invalid option: clientcert=" + value);
			} else if (key.equals("ciphers")) {
				ciphers = value;
			} else if (key.equals("protocols")) {
				protocols = value;
			} else if (key.equals("keystore")) {
				keyStoreFile = value;
			} else if (key.equals("key")) {
				keyOptions = value.split(":", 3);
				if (keyOptions.length < 3) {
					String[] newOptions = new String[3];
					System.arraycopy(keyOptions, 0, newOptions, 0, keyOptions.length);
					keyOptions = newOptions;
				}
				if (keyOptions[1] != null && keyOptions[2] == null)
					keyOptions[2] = keyOptions[0];
			} else if (key.equals("truststore")) {
				if (value.equals("-")) {
					tms = new TrustManager[] { new SSLDummyTrustManager() };
				} else {
					KeyStore ks = KeyStore.getInstance("JKS");
					FileInputStream fis = new FileInputStream(value);
					ks.load(fis, null);
					fis.close();
					tmf.init(ks);
					tms = tmf.getTrustManagers();
				}
			} else {
				throw new IllegalArgumentException("Unknown SSL option: " + key);
			}
		}
		KeyManager[] kms = null;
		if (keyOptions != null) {
			KeyStore ks = KeyStore.getInstance("JKS");
			FileInputStream fis = new FileInputStream(keyStoreFile);
			char[] storePassword = keyOptions[0].toCharArray();
			if (keyOptions[1] == null && keyOptions[2] == null) {
				ks.load(fis, storePassword);
			} else {
				ks.load(null, null);
				KeyStore sourceKS = KeyStore.getInstance("JKS");
				sourceKS.load(fis, storePassword);
				Key k = sourceKS.getKey(keyOptions[1], keyOptions[2].toCharArray());
				Certificate[] chain = sourceKS.getCertificateChain(keyOptions[1]);
				ks.setKeyEntry("mykey", k, storePassword, chain);
			}
			fis.close();
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, storePassword);
			kms = kmf.getKeyManagers();
		}
		SSLContext ctx = SSLContext.getInstance("SSL");
		ctx.init(kms, tms, null);
		String[] cipherList = null, protocolList = null;
		if (ciphers != null || protocols != null) {
			SSLSocket s = (SSLSocket) ctx.getSocketFactory().createSocket();
			if (ciphers != null)
				cipherList = parseListPattern("ciphers", ciphers, s.getSupportedCipherSuites(), s.getEnabledCipherSuites());
			if (protocols != null)
				protocolList = parseListPattern("protocols", protocols, s.getSupportedProtocols(), s.getEnabledProtocols());
		}
		return new SSLOptions(ctx, rule, listener, clientCert, cipherList, protocolList);
	}

	private static String[] parseListPattern(String name, String value, String[] supportedOptions, String[] enabledOptions) {
		Pattern includeRegex, excludeRegex;
		int pos = value.indexOf('!');
		if (pos == -1) {
			includeRegex = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
			excludeRegex = null;
		} else {
			includeRegex = Pattern.compile(pos == 0 ? "ENABLED:.*" : value.substring(0, pos), Pattern.CASE_INSENSITIVE);
			excludeRegex = Pattern.compile(value.substring(pos + 1), Pattern.CASE_INSENSITIVE);
		}
		List/* <String> */enabled = Arrays.asList(enabledOptions);
		List/* <String> */result = new ArrayList();
		for (int i = 0; i < supportedOptions.length; i++) {
			String option = supportedOptions[i];
			String prefix = enabled.contains(option) ? "Enabled:" : "Disabled:";
			if (includeRegex.matcher(option).matches() || includeRegex.matcher(prefix + option).matches()) {
				if (excludeRegex == null || (!excludeRegex.matcher(option).matches() && !excludeRegex.matcher(prefix + option).matches())) {
					result.add(option);
				}
			}
		}
		if (result.size() == 0) {
			System.out.println("Supported " + name + ":");
			for (int i = 0; i < supportedOptions.length; i++) {
				String option = supportedOptions[i];
				String prefix = enabled.contains(option) ? "Enabled:" : "Disabled:";
				System.out.println("\t" + prefix + option);
			}
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	public synchronized SSLSocket createSocket() throws IOException {
		if (ssf == null)
			ssf = ctx.getSocketFactory();
		SSLSocket socket = (SSLSocket) ssf.createSocket();
		socket.setUseClientMode(!serverMode);
		if (clientCert == CLIENT_CERT_WANTED)
			socket.setWantClientAuth(true);
		else if (clientCert == CLIENT_CERT_NEEDED)
			socket.setNeedClientAuth(true);
		if (protocols != null)
			socket.setEnabledProtocols(protocols);
		if (ciphers != null)
			socket.setEnabledCipherSuites(ciphers);
		return socket;
	}

	public SSLServerSocket createServerSocket(int port, InetAddress bindAddr) throws IOException {
		if (sssf == null)
			sssf = getContext().getServerSocketFactory();
		SSLServerSocket ss = (SSLServerSocket) sssf.createServerSocket(port, 50, bindAddr);
		ss.setUseClientMode(!serverMode);
		if (clientCert == CLIENT_CERT_WANTED)
			ss.setWantClientAuth(true);
		else if (clientCert == CLIENT_CERT_NEEDED)
			ss.setNeedClientAuth(true);
		if (protocols != null)
			ss.setEnabledProtocols(protocols);
		if (ciphers != null)
			ss.setEnabledCipherSuites(ciphers);
		return ss;
	}

	public static final class SSLDummyTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}
	}
}
