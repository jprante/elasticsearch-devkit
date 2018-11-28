package org.elasticsearch.testframework.common.io;

import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.io.PathUtils;
import org.junit.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.elasticsearch.testframework.hamcrest.ElasticsearchAssertions.assertFileExists;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/** test helper methods for working with files */
public class FileTestUtils {

    /**
     * Check that a file contains a given String
     * @param dir root dir for file
     * @param filename relative path from root dir to file
     * @param expected expected content (if null, we don't expect any file)
     */
    public static void assertFileContent(Path dir, String filename, String expected) throws IOException {
        Assert.assertThat(Files.exists(dir), is(true));
        Path file = dir.resolve(filename);
        if (expected == null) {
            Assert.assertThat("file [" + file + "] should not exist.", Files.exists(file), is(false));
        } else {
            assertFileExists(file);
            String fileContent = new String(Files.readAllBytes(file), java.nio.charset.StandardCharsets.UTF_8);
            // trim the string content to prevent different handling on windows vs. unix and CR chars...
            Assert.assertThat(fileContent.trim(), equalTo(expected.trim()));
        }
    }

    /**
     * Unzip a zip file to a destination directory.  If the zip file does not exist, an IOException is thrown.
     * If the destination directory does not exist, it will be created.
     *
     * @param zip      zip file to unzip
     * @param destDir  directory to unzip the file to
     * @param prefixToRemove  the (optional) prefix in the zip file path to remove when writing to the destination directory
     * @throws IOException if zip file does not exist, or there was an error reading from the zip file or
     *                     writing to the destination directory
     */
    public static void unzip(final Path zip, final Path destDir, @Nullable final String prefixToRemove) throws IOException {
        if (Files.notExists(zip)) {
            throw new IOException("[" + zip + "] zip file must exist");
        }
        Files.createDirectories(destDir);

        try (ZipInputStream zipInput = new ZipInputStream(Files.newInputStream(zip))) {
            ZipEntry entry;
            while ((entry = zipInput.getNextEntry()) != null) {
                final String entryPath;
                if (prefixToRemove != null) {
                    if (entry.getName().startsWith(prefixToRemove)) {
                        entryPath = entry.getName().substring(prefixToRemove.length());
                    } else {
                        throw new IOException("prefix not found: " + prefixToRemove);
                    }
                } else {
                    entryPath = entry.getName();
                }
                final Path path = PathUtils.get(destDir.toString(), entryPath);
                if (entry.isDirectory()) {
                    Files.createDirectories(path);
                } else {
                    Files.copy(zipInput, path);
                }
                zipInput.closeEntry();
            }
        }
    }
}
