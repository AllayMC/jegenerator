package cn.allay.jegenerator.utils;

public class BlockUtils {
    public static String getCleanIdentifier(String fullJavaIdentifier) {
        int stateIndex = fullJavaIdentifier.indexOf('[');
        if (stateIndex == -1) {
            // Identical to its clean variation
            return fullJavaIdentifier;
        }
        return fullJavaIdentifier.substring(0, stateIndex);
    }
}
