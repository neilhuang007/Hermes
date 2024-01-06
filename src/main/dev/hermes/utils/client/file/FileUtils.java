package dev.hermes.utils.client.file;

import dev.hermes.Hermes;

import java.io.*;

public class FileUtils {
    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } catch (Exception e) {
            // Don't give crackers clues...
            if (Hermes.DEVELOPMENT_SWITCH)
                e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static String readPath(String path) {
        StringBuilder builder = new StringBuilder();

        InputStream is = null;
        try {
            is = FileUtils.class.getResourceAsStream(path);
            if (is == null) return null;

            int b;
            while ((b = is.read()) != -1) {
                builder.append((char) b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return builder.toString();
    }

    public static void createDir(File file) {
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static void createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // Don't give crackers clues...
                if (Hermes.DEVELOPMENT_SWITCH)
                    e.printStackTrace();
            }
        }
    }
}
