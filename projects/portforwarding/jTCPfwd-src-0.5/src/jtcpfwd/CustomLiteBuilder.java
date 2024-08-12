package jtcpfwd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import jtcpfwd.destination.Destination;
import jtcpfwd.forwarder.Forwarder;
import jtcpfwd.forwarder.filter.Filter;
import jtcpfwd.listener.Listener;
import jtcpfwd.listener.knockrule.KnockRule;

public class CustomLiteBuilder {

	public static final String[] TYPES = { "Listener", "Forwarder", "Destination", "Filter", "KnockRule", "Main Class" };

	public static final Class[] BASECLASS = {
			Listener.class, Forwarder.class, Destination.class, Filter.class, KnockRule.class,
			Main.class // last one!
	};

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Usage: java jtcpfwd.CustomLiteBuilder <listeners> -- <forwarders> ");
			System.out.println("                         [-- <destinations> [-- <filters> [-- <knockrules>]]]");
			System.out.println();
			System.out.println("The result will be stored as jTCPfwd-lite-custom.jar. If some ");
			System.out.println("listeners/forwarders require a destination, it is an error if none is given.");
			System.out.println("Some listeners and forwarders cannot be placed inside a lite jar, because of ");
			System.out.println("library dependencies.");
			System.out.println("Instead of a listener/forwarder name, you can also give a full ");
			System.out.println("listener/forwarder rule, which will temporarily start that listener or");
			System.out.println("forwarder to find out which modules are needed by it.");
		} else {
			HashSet/* <String> */classNames = new HashSet();
			int[] counters = new int[BASECLASS.length];
			int typeIndex = 0;
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--")) {
					typeIndex++;
					if (typeIndex >= TYPES.length - 1)
						throw new IllegalArgumentException("Too many \"--\" in command line");
				} else if (typeIndex < 2 && args[i].indexOf('@') != -1) {
					Module module = Lookup.lookupClass(BASECLASS[typeIndex], args[i]);
					addRequiredClasses(counters, classNames, module);
					module.dispose();
				} else {
					addRequiredClasses(classNames, Module.lookup(BASECLASS[typeIndex], args[i]));
					counters[typeIndex]++;
				}
			}
			if (counters[0] == 0 || counters[1] == 0) {
				throw new IllegalArgumentException("At least one Listener and at least one Forwarder required");
			}
			if (counters[2] == 0 && classNames.contains(Destination.class.getName())) {
				throw new IllegalArgumentException("Modules require Destination but no Destination selected");
			}
			if (counters[3] == 0 && classNames.contains(Filter.class.getName())) {
				throw new IllegalArgumentException("Modules require Filter but no Filter selected");
			}
			if (counters[4] == 0 && classNames.contains(KnockRule.class.getName())) {
				throw new IllegalArgumentException("Modules require KnockRule but no KnockRule selected");
			}
			addRequiredClasses(classNames, Main.class);
			buildJAR(classNames);
			System.out.println("jTCPfwd-lite-custom.jar created.");
		}
	}

	public static void addRequiredClasses(int[] counters, Set classNames, Module module) throws Exception {
		Class moduleClass = module.getClass();
		addRequiredClasses(classNames, moduleClass);
		for (int i = 0; i < BASECLASS.length; i++) {
			if (BASECLASS[i].isAssignableFrom(moduleClass)) {
				counters[i]++;
				moduleClass = null;
				break;
			}
		}
		if (moduleClass != null)
			throw new RuntimeException("Unsupported module: " + moduleClass);
		Module[] usedModules = module.getUsedModules();
		for (int i = 0; i < usedModules.length; i++) {
			addRequiredClasses(counters, classNames, usedModules[i]);
		}
	}

	public static void addRequiredClasses(Set classNames, Class moduleClass) throws Exception {
		Class baseClass = null;
		for (int i = 0; i < BASECLASS.length; i++) {
			if (BASECLASS[i].isAssignableFrom(moduleClass)) {
				baseClass = BASECLASS[i];
				break;
			}
		}
		if (baseClass == null)
			throw new IllegalArgumentException("Unsupported module class");
		classNames.add(Module.class.getName());
		classNames.add(baseClass.getName());
		if (baseClass.getName().equals(Listener.class.getName()))
			classNames.add(NoMoreSocketsException.class.getName());
		if (baseClass.getName().equals(Filter.class.getName()))
			classNames.add(Filter.Parameters.class.getName());
		classNames.add(moduleClass.getName());
		try {
			Class[] classes = (Class[]) moduleClass.getMethod("getRequiredClasses", new Class[0]).invoke(null, new Object[0]);
			for (int i = 0; i < classes.length; i++) {
				classNames.add(classes[i].getName());
			}
		} catch (NoSuchMethodException ex) {
			// ignore
		}
	}

	private static void buildJAR(Set classNames) throws IOException {
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
		manifest.getMainAttributes().putValue("Main-Class", "jtcpfwd.Main");
		final JarOutputStream jos = new JarOutputStream(new FileOutputStream("jTCPfwd-lite-custom.jar"), manifest);
		final byte[] buf = new byte[4096];
		int len;
		for (Iterator it = classNames.iterator(); it.hasNext();) {
			String className = (String) it.next();
			final String classFileName = className.replace('.', '/') + ".class";
			jos.putNextEntry(new ZipEntry(classFileName));
			final InputStream in = CustomLiteBuilder.class.getResourceAsStream("/" + classFileName);
			while ((len = in.read(buf)) != -1) {
				jos.write(buf, 0, len);
			}
			in.close();
		}
		jos.close();
	}
}
