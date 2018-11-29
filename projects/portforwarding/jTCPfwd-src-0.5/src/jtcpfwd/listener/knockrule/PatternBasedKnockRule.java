package jtcpfwd.listener.knockrule;

import java.io.IOException;

import jtcpfwd.util.BinaryExpression;
import jtcpfwd.util.MaskedBinaryExpression;

public abstract class PatternBasedKnockRule extends KnockRule {

	protected final MaskedBinaryExpression matchExpression;
	protected final byte[] response;

	// args: [<startArg other args>][#<bytes>[#<response>]]
	public PatternBasedKnockRule(String[] args, int startArg) throws IOException {
		String bytes, ok = "";
		if (args.length == startArg) {
			bytes = "unescape:friend\\r\\n";
		} else if (args.length == startArg + 1) {
			bytes = args[startArg];
		} else if (args.length == startArg + 2) {
			bytes = args[startArg];
			ok = args[startArg + 1];
		} else {
			throw new IllegalArgumentException("Invalid number of parameters");
		}
		matchExpression = new MaskedBinaryExpression(bytes);
		response = ok.length() == 0 ? null : BinaryExpression.parseBinaryExpression(ok);
	}
}
