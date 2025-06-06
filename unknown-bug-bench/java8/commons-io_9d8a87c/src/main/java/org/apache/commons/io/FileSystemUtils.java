/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * General File System utilities.
 * <p>
 * This class provides static utility methods for general file system
 * functions not provided via the JDK {@link java.io.File File} class.
 * <p>
 * The current functions provided are:
 * <ul>
 * <li>Get the free space on a drive
 * </ul>
 *
 * @since 1.1
 * @deprecated As of 2.6 deprecated without replacement. Use equivalent
 *  methods in {@link java.nio.file.FileStore} instead, e.g.
 *  {@code Files.getFileStore(Paths.get("/home")).getUsableSpace()}
 *  or iterate over {@code FileSystems.getDefault().getFileStores()}
 */
@Deprecated
public class FileSystemUtils {

    /** Singleton instance, used mainly for testing. */
    private static final FileSystemUtils INSTANCE = new FileSystemUtils();

    /** Operating system state flag for error. */
    private static final int INIT_PROBLEM = -1;
    /** Operating system state flag for neither Unix nor Windows. */
    private static final int OTHER = 0;
    /** Operating system state flag for Windows. */
    private static final int WINDOWS = 1;
    /** Operating system state flag for Unix. */
    private static final int UNIX = 2;
    /** Operating system state flag for Posix flavor Unix. */
    private static final int POSIX_UNIX = 3;

    /** The operating system flag. */
    private static final int OS;

    /** The path to df */
    private static final String DF;

    static {
        int os = OTHER;
        String dfPath = "df";
        try {
            String osName = System.getProperty("os.name");
            if (osName == null) {
                throw new IOException("os.name not found");
            }
            osName = osName.toLowerCase(Locale.ENGLISH);
            // match
            if (osName.contains("windows")) {
                os = WINDOWS;
            } else if (osName.contains("linux") ||
                    osName.contains("mpe/ix") ||
                    osName.contains("freebsd") ||
                    osName.contains("openbsd") ||
                    osName.contains("irix") ||
                    osName.contains("digital unix") ||
                    osName.contains("unix") ||
                    osName.contains("mac os x")) {
                os = UNIX;
            } else if (osName.contains("sun os") ||
                    osName.contains("sunos") ||
                    osName.contains("solaris")) {
                os = POSIX_UNIX;
                dfPath = "/usr/xpg4/bin/df";
            } else if (osName.contains("hp-ux") ||
                    osName.contains("aix")) {
                os = POSIX_UNIX;
            }

        } catch (final Exception ex) {
            os = INIT_PROBLEM;
        }
        OS = os;
        DF = dfPath;
    }

    /**
     * Returns the free space on a drive or volume by invoking
     * the command line.
     * This method does not normalize the result, and typically returns
     * bytes on Windows, 512 byte units on OS X and kilobytes on Unix.
     * As this is not very useful, this method is deprecated in favor
     * of {@link #freeSpaceKb(String)} which returns a result in kilobytes.
     * <p>
     * Note that some OS's are NOT currently supported, including OS/390,
     * OpenVMS.
     * <pre>
     * FileSystemUtils.freeSpace("C:");       // Windows
     * FileSystemUtils.freeSpace("/volume");  // *nix
     * </pre>
     * The free space is calculated via the command line.
     * It uses 'dir /-c' on Windows and 'df' on *nix.
     *
     * @param path  the path to get free space for, not null, not empty on Unix
     * @return the amount of free drive space on the drive or volume
     * @throws IllegalArgumentException if the path is invalid
     * @throws IllegalStateException if an error occurred in initialization
     * @throws IOException if an error occurs when finding the free space
     * @since 1.1, enhanced OS support in 1.2 and 1.3
     * @deprecated Use freeSpaceKb(String)
     *  Deprecated from 1.3, may be removed in 2.0
     */
    @Deprecated
    public static long freeSpace(final String path) throws IOException {
        return INSTANCE.freeSpaceOS(path, OS, false, Duration.ofMillis(-1));
    }

    /**
     * Returns the free space for the working directory
     * in kibibytes (1024 bytes) by invoking the command line.
     * <p>
     * Identical to:
     * <pre>
     * freeSpaceKb(FileUtils.current().getAbsolutePath())
     * </pre>
     * @return the amount of free drive space on the drive or volume in kilobytes
     * @throws IllegalStateException if an error occurred in initialization
     * @throws IOException if an error occurs when finding the free space
     * @since 2.0
     * @deprecated As of 2.6 deprecated without replacement. Please use {@link java.nio.file.FileStore#getUsableSpace()}.
     */
    @Deprecated
    public static long freeSpaceKb() throws IOException {
        return freeSpaceKb(-1);
    }

    /**
     * Returns the free space for the working directory
     * in kibibytes (1024 bytes) by invoking the command line.
     * <p>
     * Identical to:
     * <pre>
     * freeSpaceKb(FileUtils.current().getAbsolutePath())
     * </pre>
     * @param timeout The timeout amount in milliseconds or no timeout if the value
     *  is zero or less
     * @return the amount of free drive space on the drive or volume in kilobytes
     * @throws IllegalStateException if an error occurred in initialization
     * @throws IOException if an error occurs when finding the free space
     * @since 2.0
     * @deprecated As of 2.6 deprecated without replacement. Please use {@link java.nio.file.FileStore#getUsableSpace()}.
     */
    @Deprecated
    public static long freeSpaceKb(final long timeout) throws IOException {
        return freeSpaceKb(FileUtils.current().getAbsolutePath(), timeout);
    }
    /**
     * Returns the free space on a drive or volume in kibibytes (1024 bytes)
     * by invoking the command line.
     * <pre>
     * FileSystemUtils.freeSpaceKb("C:");       // Windows
     * FileSystemUtils.freeSpaceKb("/volume");  // *nix
     * </pre>
     * The free space is calculated via the command line.
     * It uses 'dir /-c' on Windows, 'df -kP' on AIX/HP-UX and 'df -k' on other Unix.
     * <p>
     * In order to work, you must be running Windows, or have an implementation of
     * Unix df that supports GNU format when passed -k (or -kP). If you are going
     * to rely on this code, please check that it works on your OS by running
     * some simple tests to compare the command line with the output from this class.
     * If your operating system isn't supported, please raise a JIRA call detailing
     * the exact result from df -k and as much other detail as possible, thanks.
     *
     * @param path  the path to get free space for, not null, not empty on Unix
     * @return the amount of free drive space on the drive or volume in kilobytes
     * @throws IllegalArgumentException if the path is invalid
     * @throws IllegalStateException if an error occurred in initialization
     * @throws IOException if an error occurs when finding the free space
     * @since 1.2, enhanced OS support in 1.3
     * @deprecated As of 2.6 deprecated without replacement. Please use {@link java.nio.file.FileStore#getUsableSpace()}.
     */
    @Deprecated
    public static long freeSpaceKb(final String path) throws IOException {
        return freeSpaceKb(path, -1);
    }

    /**
     * Returns the free space on a drive or volume in kibibytes (1024 bytes)
     * by invoking the command line.
     * <pre>
     * FileSystemUtils.freeSpaceKb("C:");       // Windows
     * FileSystemUtils.freeSpaceKb("/volume");  // *nix
     * </pre>
     * The free space is calculated via the command line.
     * It uses 'dir /-c' on Windows, 'df -kP' on AIX/HP-UX and 'df -k' on other Unix.
     * <p>
     * In order to work, you must be running Windows, or have an implementation of
     * Unix df that supports GNU format when passed -k (or -kP). If you are going
     * to rely on this code, please check that it works on your OS by running
     * some simple tests to compare the command line with the output from this class.
     * If your operating system isn't supported, please raise a JIRA call detailing
     * the exact result from df -k and as much other detail as possible, thanks.
     *
     * @param path  the path to get free space for, not null, not empty on Unix
     * @param timeout The timeout amount in milliseconds or no timeout if the value
     *  is zero or less
     * @return the amount of free drive space on the drive or volume in kilobytes
     * @throws IllegalArgumentException if the path is invalid
     * @throws IllegalStateException if an error occurred in initialization
     * @throws IOException if an error occurs when finding the free space
     * @since 2.0
     * @deprecated As of 2.6 deprecated without replacement. Please use {@link java.nio.file.FileStore#getUsableSpace()}.
     */
    @Deprecated
    public static long freeSpaceKb(final String path, final long timeout) throws IOException {
        return INSTANCE.freeSpaceOS(path, OS, true, Duration.ofMillis(timeout));
    }

    /**
     * Instances should NOT be constructed in standard programming.
     *
     * @deprecated TODO Make private in 3.0.
     */
    @Deprecated
    public FileSystemUtils() {
        // empty
    }

    /**
     * Returns the free space on a drive or volume in a cross-platform manner.
     * Note that some OS's are NOT currently supported, including OS/390.
     * <pre>
     * FileSystemUtils.freeSpace("C:");  // Windows
     * FileSystemUtils.freeSpace("/volume");  // *nix
     * </pre>
     * The free space is calculated via the command line.
     * It uses 'dir /-c' on Windows and 'df' on *nix.
     *
     * @param path  the path to get free space for, not null, not empty on Unix
     * @param os  the operating system code
     * @param kb  whether to normalize to kilobytes
     * @param timeout The timeout amount in milliseconds or no timeout if the value
     *  is zero or less
     * @return the amount of free drive space on the drive or volume
     * @throws IllegalArgumentException if the path is invalid
     * @throws IllegalStateException if an error occurred in initialization
     * @throws IOException if an error occurs when finding the free space
     */
    long freeSpaceOS(final String path, final int os, final boolean kb, final Duration timeout) throws IOException {
        Objects.requireNonNull(path, "path");
        switch (os) {
        case WINDOWS:
            return kb ? freeSpaceWindows(path, timeout) / FileUtils.ONE_KB : freeSpaceWindows(path, timeout);
        case UNIX:
            return freeSpaceUnix(path, kb, false, timeout);
        case POSIX_UNIX:
            return freeSpaceUnix(path, kb, true, timeout);
        case OTHER:
            throw new IllegalStateException("Unsupported operating system");
        default:
            throw new IllegalStateException("Exception caught when determining operating system");
        }
    }

    /**
     * Find free space on the *nix platform using the 'df' command.
     *
     * @param path  the path to get free space for
     * @param kb  whether to normalize to kilobytes
     * @param posix  whether to use the POSIX standard format flag
     * @param timeout The timeout amount in milliseconds or no timeout if the value
     *  is zero or less
     * @return the amount of free drive space on the volume
     * @throws IOException If an I/O error occurs
     */
    long freeSpaceUnix(final String path, final boolean kb, final boolean posix, final Duration timeout)
            throws IOException {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path must not be empty");
        }

        // build and run the 'dir' command
        String flags = "-";
        if (kb) {
            flags += "k";
        }
        if (posix) {
            flags += "P";
        }
        final String[] cmdAttribs = flags.length() > 1 ? new String[] { DF, flags, path } : new String[] { DF, path };

        // perform the command, asking for up to 3 lines (header, interesting, overflow)
        final List<String> lines = performCommand(cmdAttribs, 3, timeout);
        if (lines.size() < 2) {
            // unknown problem, throw exception
            throw new IOException("Command line '" + DF + "' did not return info as expected for path '" + path + "'- response was " + lines);
        }
        final String line2 = lines.get(1); // the line we're interested in

        // Now, we tokenize the string. The fourth element is what we want.
        StringTokenizer tok = new StringTokenizer(line2, " ");
        if (tok.countTokens() < 4) {
            // could be long Filesystem, thus data on third line
            if (tok.countTokens() != 1 || lines.size() < 3) {
                throw new IOException("Command line '" + DF + "' did not return data as expected for path '" + path + "'- check path is valid");
            }
            final String line3 = lines.get(2); // the line may be interested in
            tok = new StringTokenizer(line3, " ");
        } else {
            tok.nextToken(); // Ignore Filesystem
        }
        tok.nextToken(); // Ignore 1K-blocks
        tok.nextToken(); // Ignore Used
        final String freeSpace = tok.nextToken();
        return parseBytes(freeSpace, path);
    }

    /**
     * Find free space on the Windows platform using the 'dir' command.
     *
     * @param path  the path to get free space for, including the colon
     * @param timeout The timeout amount in milliseconds or no timeout if the value
     *  is zero or less
     * @return the amount of free drive space on the drive
     * @throws IOException If an I/O error occurs
     */
    long freeSpaceWindows(final String path, final Duration timeout) throws IOException {
        String normPath = FilenameUtils.normalize(path, false);
        if (normPath == null) {
            throw new IllegalArgumentException(path);
        }
        if (!normPath.isEmpty() && normPath.charAt(0) != '"') {
            normPath = "\"" + normPath + "\"";
        }

        // build and run the 'dir' command
        final String[] cmdAttribs = { "cmd.exe", "/C", "dir /a /-c " + normPath };

        // read in the output of the command to an ArrayList
        final List<String> lines = performCommand(cmdAttribs, Integer.MAX_VALUE, timeout);

        // now iterate over the lines we just read and find the LAST
        // non-empty line (the free space bytes should be in the last element
        // of the ArrayList anyway, but this will ensure it works even if it's
        // not, still assuming it is on the last non-blank line)
        for (int i = lines.size() - 1; i >= 0; i--) {
            final String line = lines.get(i);
            if (!line.isEmpty()) {
                return parseDir(line, normPath);
            }
        }
        // all lines are blank
        throw new IOException("Command line 'dir /-c' did not return any info for path '" + normPath + "'");
    }

    /**
     * Opens the process to the operating system.
     *
     * @param cmdAttribs  the command line parameters
     * @return the process
     * @throws IOException If an I/O error occurs
     */
    Process openProcess(final String[] cmdAttribs) throws IOException {
        return Runtime.getRuntime().exec(cmdAttribs);
    }

    /**
     * Parses the bytes from a string.
     *
     * @param freeSpace  the free space string
     * @param path  the path
     * @return the number of bytes
     * @throws IOException If an I/O error occurs
     */
    long parseBytes(final String freeSpace, final String path) throws IOException {
        try {
            final long bytes = Long.parseLong(freeSpace);
            if (bytes < 0) {
                throw new IOException("Command line '" + DF + "' did not find free space in response for path '" + path + "'- check path is valid");
            }
            return bytes;

        } catch (final NumberFormatException ex) {
            throw new IOException("Command line '" + DF + "' did not return numeric data as expected for path '" + path + "'- check path is valid", ex);
        }
    }

    /**
     * Parses the Windows dir response last line
     *
     * @param line  the line to parse
     * @param path  the path that was sent
     * @return the number of bytes
     * @throws IOException If an I/O error occurs
     */
    long parseDir(final String line, final String path) throws IOException {
        // read from the end of the line to find the last numeric
        // character on the line, then continue until we find the first
        // non-numeric character, and everything between that and the last
        // numeric character inclusive is our free space bytes count
        int bytesStart = 0;
        int bytesEnd = 0;
        int j = line.length() - 1;
        innerLoop1: while (j >= 0) {
            final char c = line.charAt(j);
            if (Character.isDigit(c)) {
                // found the last numeric character, this is the end of
                // the free space bytes count
                bytesEnd = j + 1;
                break innerLoop1;
            }
            j--;
        }
        innerLoop2: while (j >= 0) {
            final char c = line.charAt(j);
            if (!Character.isDigit(c) && c != ',' && c != '.') {
                // found the next non-numeric character, this is the
                // beginning of the free space bytes count
                bytesStart = j + 1;
                break innerLoop2;
            }
            j--;
        }
        if (j < 0) {
            throw new IOException("Command line 'dir /-c' did not return valid info for path '" + path + "'");
        }

        // remove commas and dots in the bytes count
        final StringBuilder buf = new StringBuilder(line.substring(bytesStart, bytesEnd));
        for (int k = 0; k < buf.length(); k++) {
            if (buf.charAt(k) == ',' || buf.charAt(k) == '.') {
                buf.deleteCharAt(k--);
            }
        }
        return parseBytes(buf.toString(), path);
    }

    /**
     * Performs an OS command.
     *
     * @param cmdAttribs  the command line parameters
     * @param max The maximum limit for the lines returned
     * @param timeout The timeout amount in milliseconds or no timeout if the value
     *  is zero or less
     * @return the lines returned by the command, converted to lower-case
     * @throws IOException if an error occurs
     */
    List<String> performCommand(final String[] cmdAttribs, final int max, final Duration timeout) throws IOException {
        //
        // This method does what it can to avoid the 'Too many open files' error
        // based on trial and error and these links:
        // https://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4784692
        // https://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4801027
        // https://forum.java.sun.com/thread.jspa?threadID=533029&messageID=2572018
        // however, it's still not perfect as the JDK support is so poor
        // (see commons-exec or Ant for a better multithreaded multi-OS solution)
        //
        final Process proc = openProcess(cmdAttribs);
        final Thread monitor = ThreadMonitor.start(timeout);
        try (InputStream in = proc.getInputStream();
                OutputStream out = proc.getOutputStream();
                // default Charset is most likely appropriate here
                InputStream err = proc.getErrorStream();
                // If in is null here, InputStreamReader throws NullPointerException
                BufferedReader inr = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()))) {

            final List<String> lines = inr.lines().limit(max).map(line -> line.toLowerCase(Locale.getDefault()).trim()).collect(Collectors.toList());
            proc.waitFor();
            ThreadMonitor.stop(monitor);

            if (proc.exitValue() != 0) {
                // Command problem, throw exception
                throw new IOException("Command line returned OS error code '" + proc.exitValue() + "' for command " + Arrays.asList(cmdAttribs));
            }
            if (lines.isEmpty()) {
                // Unknown problem, throw exception
                throw new IOException("Command line did not return any info for command " + Arrays.asList(cmdAttribs));
            }

            return lines;

        } catch (final InterruptedException ex) {
            throw new IOException("Command line threw an InterruptedException for command " + Arrays.asList(cmdAttribs) + " timeout=" + timeout, ex);
        } finally {
            if (proc != null) {
                proc.destroy();
            }
        }
    }

}
