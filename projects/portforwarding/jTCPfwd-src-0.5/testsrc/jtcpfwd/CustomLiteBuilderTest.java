package jtcpfwd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;

import jtcpfwd.destination.Destination;
import jtcpfwd.forwarder.FilterForwarder;
import jtcpfwd.forwarder.filter.Filter;
import jtcpfwd.listener.KnockListener;
import jtcpfwd.listener.knockrule.KnockRule;

public class CustomLiteBuilderTest {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			buildAntFile(false);
		} else if (args.length == 1 && args[0].equals("-full")) {
			buildAntFile(true);
		} else {
			checkFiles(new File("build/testbuild"), "", args);
		}
	}

	private static void buildAntFile(boolean full) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter("build/testCustomLite.xml"));

		bw.write("<?xml version=\"1.0\"?>\r\n" +
				"<project name=\"jTCPfwdTestCustomLiteBuilder\" default=\"test\">\r\n" +
				"	<target name=\"test\">\r\n");

		buildAntFileSection(full, bw, 5, new String[] { "" });
		buildAntFileSection(full, bw, 0, Main.SUPPORTED_LISTENERS);
		buildAntFileSection(full, bw, 1, Main.SUPPORTED_FORWARDERS);
		buildAntFileSection(full, bw, 2, Main.SUPPORTED_DESTINATIONS);
		buildAntFileSection(full, bw, 3, FilterForwarder.SUPPORTED_FILTERS);
		buildAntFileSection(full, bw, 4, KnockListener.SUPPORTED_KNOCK_RULES);

		bw.write("		<echo message=\"*** All tests done ***\" />\r\n" +
				"	</target>\r\n" +
				"</project>");

		bw.close();
		if (full) {
			bw = new BufferedWriter(new FileWriter("build/TemplateClass"));
			bw.write("package @PACKAGE@;\r\n" +
					"\r\n" +
					"public class @CLASS@ {\r\n" +
					"	private @CLASS@() {}\r\n" +
					"}\r\n");
			bw.close();
		}
	}

	private static void buildAntFileSection(boolean full, BufferedWriter bw, int typeIndex, String[] modules) throws Exception {
		for (int i = 0; i < modules.length; i++) {
			String module = modules[i];
			HashSet hs = new HashSet();
			Class clazz;
			try {
				clazz = Module.lookup(CustomLiteBuilder.BASECLASS[typeIndex], module);
				CustomLiteBuilder.addRequiredClasses(hs, clazz);
			} catch (ClassNotFoundException ex) {
				if (!module.equals("HTTPTunnel"))
					throw ex;
				System.out.println("Skipping " + module);
				continue;
			} catch (InvocationTargetException ex) {
				if (ex.getCause() instanceof UnsupportedOperationException) {
					System.out.println("Skipping " + module);
					continue;
				} else {
					throw ex;
				}
			}
			if (hs.contains(Lookup.class.getName()) ||
					(hs.contains(Destination.class.getName()) && typeIndex != 2) ||
					(hs.contains(Filter.class.getName()) && typeIndex != 3) ||
					(hs.contains(KnockRule.class.getName()) && typeIndex != 4)) {
				if (!clazz.getName().equals(Main.class.getName()) && clazz.getMethod("getUsedModules", new Class[0]).getDeclaringClass().getName().equals(Module.class.getName())) {
					throw new IllegalStateException("Class " + clazz.getName() + " has to override getUsedModules!");
				}
			}
			String[] classes = (String[]) hs.toArray(new String[hs.size()]);

			bw.write("		<echo message=\"*** Testing " + (CustomLiteBuilder.TYPES[typeIndex] + " " + module) + " ***\" />\r\n" +
					"		<delete dir=\"testsrc\" />\r\n" +
					"		<mkdir dir=\"testsrc\" />\r\n" +
					"		<copy todir=\"testsrc\">\r\n" +
					"			<fileset dir=\"testsource\">\r\n");
			for (int j = 0; j < classes.length; j++) {
				if (classes[j].indexOf('$') == -1)
					bw.write("			    <include name=\"" + classes[j].replace('.', '/') + ".java\"/>\r\n");
			}
			bw.write("			</fileset>\r\n" +
					"		</copy>\r\n" +
					"		<delete dir=\"testbuild\" />\r\n" +
					"		<mkdir dir=\"testbuild\" />\r\n" +
					"		<javac srcdir=\"testsrc\" destdir=\"testbuild\" source=\"1.1\" target=\"1.1\" />\r\n" +
					"		<java classname=\"jtcpfwd.CustomLiteBuilderTest\" failonerror=\"true\">\r\n");
			for (int j = 0; j < classes.length; j++) {
				bw.write("			<arg value=\"" + classes[j].replace('.', '/') + ".class\"/>\r\n");
			}
			bw.write("			<classpath>\r\n" +
					"				<pathelement path=\"lite\" />\r\n" +
					"				<pathelement path=\"full\" />\r\n" +
					"				<pathelement path=\"test\" />\r\n" +
					"			</classpath>\r\n" +
					"		</java>\r\n");
			if (full) {
				for (int k = 0; k < classes.length; k++) {
					if (classes[k].equals(clazz.getName()) || classes[k].indexOf('$') != -1)
						continue;
					bw.write("		<delete dir=\"testsrc\" />\r\n" +
							"		<mkdir dir=\"testsrc\" />\r\n" +
							"		<copy todir=\"testsrc\">\r\n" +
							"			<fileset dir=\"testsource\">\r\n");
					for (int j = 0; j < classes.length; j++) {
						if (j != k && classes[j].indexOf('$') == -1)
							bw.write("			    <include name=\"" + classes[j].replace('.', '/') + ".java\"/>\r\n");
					}
					int pos = classes[k].lastIndexOf('.');
					String packageName = classes[k].substring(0, pos);
					String className = classes[k].substring(pos + 1);
					bw.write("			</fileset>\r\n" +
							"		</copy>\r\n" +
							"		<copy file=\"TemplateClass\" tofile=\"testsrc/" + classes[k].replace('.', '/') + ".java\">\r\n" +
							"			<filterset>\r\n" +
							"				<filter token=\"PACKAGE\" value=\"" + packageName + "\"/>\r\n" +
							"				<filter token=\"CLASS\" value=\"" + className + "\"/>\r\n" +
							"			</filterset>\r\n" +
							"		</copy>\r\n" +
							"		<delete dir=\"testbuild\" />\r\n" +
							"		<mkdir dir=\"testbuild\" />\r\n" +
							"		<javac srcdir=\"testsrc\" destdir=\"testbuild\" source=\"1.1\" target=\"1.1\" failonerror=\"false\" errorproperty=\"extensivetest-" + typeIndex + "-" + module + "-" + classes[k] + "\"/>\r\n" +
							"		<fail unless=\"extensivetest-" + typeIndex + "-" + module + "-" + classes[k] + "\" />\r\n");
				}
			}
		}
	}

	private static void checkFiles(File dir, String prefix, String[] args) {
		System.out.println("Examining " + dir.getAbsolutePath());
		File[] files = dir.listFiles();
		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				checkFiles(files[i], prefix + files[i].getName() + "/", args);
			} else if (!Arrays.asList(args).contains(prefix + files[i].getName())) {
				System.err.println("Incorrectly built file: " + prefix + files[i].getName());
				System.exit(1);
			}
		}
	}
}
