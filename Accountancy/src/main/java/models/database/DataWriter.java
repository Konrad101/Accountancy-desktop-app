package models.database;

import java.io.*;

public class DataWriter {
    private final String fileName;

    public DataWriter(String path) {
        this.fileName = path;
    }

    public boolean saveInfo(String[][] data, boolean append) {
        try {
            File file = new File(this.fileName);
            PrintWriter writer = new PrintWriter(new FileOutputStream(file, append));
            String[] parsedData = parseData(data);
            if (parsedData == null) {
                return false;
            }

            for (String line : parsedData) {
                String lineWithoutNewLine = line.substring(0, line.length() - 1);
                if (lineWithoutNewLine.contains("\n")) {
                    lineWithoutNewLine = lineWithoutNewLine.replace("\n", "");
                    line = lineWithoutNewLine + "\n";
                }

                writer.append(line);
            }

            writer.flush();
            writer.close();
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    private String[] parseData(String[][] data) {
        if (data == null) {
            return null;
        }

        String[] parsedData = new String[data.length];
        if (data.length == 0) {
            return null;
        } else if (data[0].length == 0) {
            return null;
        }

        for (int i = 0; i < data.length; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < data[i].length; j++) {
                // □ - old replacement
                builder.append(data[i][j].replace(";", "~"));

                if (j != data[i].length - 1) {
                    builder.append(";");
                } else {
                    builder.append("\n");
                }
            }

            parsedData[i] = builder.toString();
        }

        return parsedData;
    }
}
