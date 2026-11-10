package org.gradle.util;

import java.util.Objects;

/**
 * Compatibility shim for Android Studio tooling plugin when running on Gradle 9.0+.
 * In Gradle 9, org.gradle.util.VersionNumber was removed from Gradle's public distribution,
 * but older/current Android Studio sync tooling still calls VersionNumber.parse() during IDE sync.
 */
public class VersionNumber implements Comparable<VersionNumber> {
    private static final VersionNumber UNKNOWN = new VersionNumber(0, 0, 0, 0, null);

    private final int major;
    private final int minor;
    private final int micro;
    private final int patch;
    private final String qualifier;

    public VersionNumber(int major, int minor, int micro, int patch, String qualifier) {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.patch = patch;
        this.qualifier = qualifier;
    }

    public int getMajor() { return major; }
    public int getMinor() { return minor; }
    public int getMicro() { return micro; }
    public int getPatch() { return patch; }
    public String getQualifier() { return qualifier; }

    public static VersionNumber parse(String versionString) {
        if (versionString == null || versionString.trim().isEmpty()) {
            return UNKNOWN;
        }
        try {
            String clean = versionString.split("-")[0];
            String[] parts = clean.split("\\.");
            int maj = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
            int min = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            int mic = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
            int pat = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;
            return new VersionNumber(maj, min, mic, pat, null);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

    @Override
    public int compareTo(VersionNumber other) {
        if (other == null) return 1;
        if (this.major != other.major) return Integer.compare(this.major, other.major);
        if (this.minor != other.minor) return Integer.compare(this.minor, other.minor);
        if (this.micro != other.micro) return Integer.compare(this.micro, other.micro);
        return Integer.compare(this.patch, other.patch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VersionNumber that = (VersionNumber) o;
        return major == that.major && minor == that.minor && micro == that.micro && patch == that.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, micro, patch);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + micro + (patch > 0 ? "." + patch : "");
    }
}
