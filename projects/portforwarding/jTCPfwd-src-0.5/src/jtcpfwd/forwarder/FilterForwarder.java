package jtcpfwd.forwarder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jtcpfwd.Lookup;
import jtcpfwd.Module;
import jtcpfwd.NoMoreSocketsException;
import jtcpfwd.forwarder.filter.Filter;
import jtcpfwd.listener.Listener;
import jtcpfwd.util.PipedSocketImpl;
import jtcpfwd.util.StreamForwarder;
import jtcpfwd.util.WrappedPipedOutputStream;

public class FilterForwarder extends Forwarder {

	public static final String[] SUPPORTED_FILTERS = {
			"Echo", "Encrypt", "Decrypt", "Flush", "FlipBit", "AddCRC", "CheckCRC", "Delay", "Throughput"
	};

	public static final String SYNTAX = "Filter@#[<outfilter>][#[<infilter>][#[<outfilter>...]]]#<forwarder>\r\n" +
			"\r\n" +
			"Supported filters: \r\n" +
			buildFilterDescription();

	private static String buildFilterDescription() {
		StringBuffer sb = new StringBuffer();
		try {
			for (int i = 0; i < SUPPORTED_FILTERS.length; i++) {
				try {
					Class clazz = Module.lookup(Filter.class, SUPPORTED_FILTERS[i]);
					sb.append("  " + (String) clazz.getField("SYNTAX").get(null) + "\r\n");
				} catch (ClassNotFoundException ex) {
					// ignore
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sb.toString();
	}

	public static final Class[] getRequiredClasses() {
		return new Class[] {
				Lookup.class, Listener.class,
				NoMoreSocketsException.class,
				Filter.class, Filter.Parameters.class,
				PipedSocketImpl.class, PipedSocketImpl.PipedSocket.class,
				WrappedPipedOutputStream.class,
				StreamForwarder.class };
	}

	private final Forwarder forwarder;
	private final Filter[] filters;

	public FilterForwarder(String rule) throws Exception {
		StringTokenizer st = new StringTokenizer(rule.substring(1), "" + rule.charAt(0), true);
		boolean needSeparator = false;
		String lastToken = "";
		List /* <Filter> */filterList = new ArrayList();
		while (st.hasMoreTokens()) {
			String t = st.nextToken();
			if (!st.hasMoreTokens()) {
				lastToken = t;
				break;
			}
			if (t.equals("" + rule.charAt(0))) {
				if (!needSeparator)
					filterList.add(null);
				needSeparator = false;
			} else {
				if (needSeparator)
					throw new IllegalStateException();
				filterList.add(lookupFilter(t));
				needSeparator = true;
			}
		}
		if (needSeparator)
			throw new IllegalArgumentException();
		filters = (Filter[]) filterList.toArray(new Filter[filterList.size()]);
		forwarder = Lookup.lookupForwarder(lastToken);
	}

	public Module[] getUsedModules() {
		return new Module[] { forwarder };
	}

	private Filter lookupFilter(String rule) throws Exception {
		int pos = rule.indexOf(",");
		String filterName = rule, parameters = null;
		if (pos != -1) {
			filterName = rule.substring(0, pos);
			parameters = rule.substring(pos + 1);
		}
		Class c = Module.lookup(Filter.class, filterName);
		return (Filter) c.getConstructor(new Class[] { String.class }).newInstance(new Object[] { parameters });
	}

	public Socket connect(Socket listener) {
		try {
			Socket innerSocket = forwarder.connect(listener);
			if (innerSocket == null)
				return null;
			PipedSocketImpl sock = new PipedSocketImpl();
			InputStream in = sock.getLocalInputStream();
			InputStream innerIn = innerSocket.getInputStream();
			OutputStream out = sock.getLocalOutputStream();
			OutputStream innerOut = innerSocket.getOutputStream();
			Filter.Parameters toInnerParameters = new Filter.Parameters(innerOut, out, listener, innerSocket);
			Filter.Parameters toOuterParameters = new Filter.Parameters(out, innerOut, innerSocket, listener);
			for (int i = 0; i < filters.length; i++) {
				if (filters[i] == null)
					continue;
				if (i % 2 == 0) {
					innerOut = filters[i].createFilterStream(innerOut, out, toInnerParameters);
				} else {
					out = filters[i].createFilterStream(out, innerOut, toOuterParameters);
				}
			}
			new StreamForwarder(innerIn, out).start();
			new StreamForwarder(in, innerOut).start();
			return sock.createSocket();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void dispose() throws IOException {
		forwarder.dispose();
		for (int i = 0; i < filters.length; i++) {
			filters[i].dispose();
		}
	}
}
