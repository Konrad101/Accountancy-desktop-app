package models.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataReader {
    private final String fileName;

    public DataReader(String path) {
        this.fileName = path;
    }

    public String[][] getFileData() {
        List<ArrayList<String>> listOfLists = new ArrayList<>();
        String[][] fileData = null;
        int defaultSize = 5;
        try {
            File file = new File(this.fileName);
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String data;
            while ((data = reader.readLine()) != null) {
                data = data.replace("\n", "");
                String[] arrOfData = data.split(";");

                for (int i = 0; i < arrOfData.length; i++) {
                    arrOfData[i] = arrOfData[i].replace('~', ';');
                }

                if (arrOfData.length > defaultSize) {
                    defaultSize = arrOfData.length;
                }

                ArrayList<String> list = new ArrayList<>(defaultSize);
                list.addAll(Arrays.asList(arrOfData));
                listOfLists.add(list);
            }
        } catch (IOException | NullPointerException ignored) {
        }

        if (listOfLists.size() > 0) {
            fileData = new String[listOfLists.size()][];
            for (int i = 0; i < listOfLists.size(); i++) {
                fileData[i] = new String[listOfLists.get(i).size()];
                for (int j = 0; j < listOfLists.get(i).size(); j++) {
                    fileData[i][j] = listOfLists.get(i).get(j);
                }
            }
        }

        return fileData;
    }

    public int readHighestID() {
        int highestId = 0;
        try {
            File file = new File(this.fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String data;
            String lastData = "";
            while ((data = reader.readLine()) != null) {
                lastData = data;
            }
            if (lastData.length() != 0) {
                lastData = lastData.replace("\n", "");
                String[] arrOfData = lastData.split(";");
                if (arrOfData.length > 0) {
                    try {
                        highestId = Integer.parseInt(arrOfData[0]);
                    } catch (NumberFormatException ex) {
                        return -2;
                    }
                }
            }
        } catch (IOException | NullPointerException ex) {
            return -1;
        }

        return highestId;
    }
}
