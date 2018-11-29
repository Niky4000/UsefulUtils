package jtcpfwd.forwarder.filter;

import java.io.IOException;
import java.io.OutputStream;

import jtcpfwd.util.SplitOutputStream;

public class EchoFilter extends Filter {

	public static final String SYNTAX = "Echo[,orig]";

	public static final Class[] getRequiredClasses() {
		return new Class[] { SplitOutputStream.class };
	}

	private boolean orig;

	public EchoFilter(String rule) throws Exception {
		orig = rule != null;
	}

	public OutputStream createFilterStream(OutputStream sameDirection, OutputStream otherDirection, Parameters parameters) throws IOException {
		return new SplitOutputStream(new OutputStream[] { sameDirection, orig ? parameters.getOtherOrig() : otherDirection });
	}
}
