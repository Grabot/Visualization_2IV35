package toolbox;

public class OperatingSystem {

	private static String system = System.getProperty("os.name").toLowerCase();

	public static String getOSforLWJGLNatives() {
		if (system.startsWith("win")) {
			return "windows";
		}

		if (system.startsWith("mac")) {
			return "macosx";
		}

		if (system.startsWith("lin")) {
			return "linux";
		}

		if (system.startsWith("sol")) {
			return "solaris";
		}
		return "unknown";
	}
}