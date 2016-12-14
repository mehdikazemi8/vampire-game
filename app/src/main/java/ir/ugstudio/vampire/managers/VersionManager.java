package ir.ugstudio.vampire.managers;

public class VersionManager {
    private static Integer[] minimumSupportVersion = null;
    private static Integer SIZE = 1024;

    private static void loadVersions() {
        minimumSupportVersion = new Integer[SIZE];
        for (int i = 0; i < SIZE; i++) {
            minimumSupportVersion[i] = SIZE;
        }

        minimumSupportVersion[8] = 8;
    }

    public static int getMinimumVersionSupport(int version) {
        if (minimumSupportVersion == null) {
            loadVersions();
        }
        if (0 <= version && version < SIZE) {
            return minimumSupportVersion[version];
        } else {
            return SIZE;
        }
    }
}
