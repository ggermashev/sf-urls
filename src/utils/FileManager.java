package utils;

import Exceptions.TableNotFoundException;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileManager {

    public static void loadTableToFile(String table, Map params) throws TableNotFoundException, IOException {
        String path = FileManager.getPath(table);

        FileWriter fileWriter = new FileWriter(path);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        String data = recursiveStringify(params);
        bufferedWriter.write(data);

        bufferedWriter.close();
    }

    public static Map loadTableFromFile(String table, Map<String, Function<Map, Object>> constructorsMap) throws TableNotFoundException, IOException {
        String path = FileManager.getPath(table);

        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String params = "";
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            params = params.concat(line);
        }
        bufferedReader.close();

        Object parsed = recursiveParse(params, constructorsMap);

        if (parsed instanceof Map) {
            return (Map) parsed;
        } else {
            return new HashMap();
        }
    }

    private static String getPath(String table) throws TableNotFoundException {
        String path = "src/TablesStorage/";
        switch (table) {
            case "User":
                path += "Users.txt";
                break;
            default:
                throw new TableNotFoundException();
        }

        return path;
    }

    public static String recursiveStringify(Object data) {
        if (data instanceof Map<?,?>) {
            return "<Map>" + ((Map<?, ?>) data).entrySet().stream().map(entry -> entry.getKey() + ":" + recursiveStringify(entry.getValue())).collect(Collectors.joining("&")) + "</Map>";
        }
        if (data instanceof List<?>) {
            return "<ArrayList>" + ((List<?>) data).stream().map(FileManager::recursiveStringify).collect(Collectors.joining(",")) + "</ArrayList>";
        }
        if (data instanceof Integer) {
            return "<Integer>" + data;
        }
        if (data instanceof Boolean) {
            return "<Boolean>" + data;
        }
        return data.toString();
    }

    private static Object recursiveParse(String data, Map<String, Function<Map, Object>> constructorsMap) {
        if (data.startsWith("<Map>")) {
            Map map = new HashMap();
            String clearData = data.substring(5, data.length() - 6);

            if (clearData.length() == 0) { return map; }

            int insideMapCounter = 0;
            StringBuilder modifiedData = new StringBuilder();

            int i = 0;
            for (Character c: clearData.toCharArray()) {
                if (insideMapCounter > 0 && c == '&') {
                    modifiedData.append("_");
                } else {
                    modifiedData.append(c);
                }

                if (i + 5 < clearData.length() && clearData.substring(i, i+5).equals("<Map>")) {
                    insideMapCounter++;
                }
                if (i + 6 < clearData.length() && clearData.substring(i, i+6).equals("</Map>")) {
                    insideMapCounter--;
                }
                i++;
            }

            clearData = modifiedData.toString();
            String[] entries = clearData.split("&");
            List<String> entriesList = Arrays.stream(entries).map(entry -> entry.replace("_", "&")).toList();

            for (String entry: entriesList) {
                if (!entry.contains(":")) {
                    continue;
                }
                String key = entry.split(":")[0];
                var value = recursiveParse(entry.split(":", 2)[1], constructorsMap);
                map.put(key, value);
            }
            return map;
        }
        if (data.startsWith("<ArrayList>")) {
            List list = new ArrayList();
            String clearData = data.substring(11, data.length() - 12);

            if (clearData.length() == 0) { return list; }

            int insideArrayCounter = 0;
            StringBuilder modifiedData = new StringBuilder();

            int i = 0;
            for (Character c: clearData.toCharArray()) {
                if (insideArrayCounter > 0 && c == ',') {
                    modifiedData.append("_");
                } else {
                    modifiedData.append(c);
                }

                if (i + 11 < clearData.length() && clearData.substring(i, i+11).equals("<ArrayList>")) {
                    insideArrayCounter++;
                }
                if (i + 12 < clearData.length() && clearData.substring(i, i+12).equals("</ArrayList>")) {
                    insideArrayCounter--;
                }
                i++;
            }

            clearData = modifiedData.toString();

            String[] elements = clearData.split(",");
            List<String> elementsList = Arrays.stream(elements).map(element -> element.replace("_", ",")).toList();

            for (String element: elementsList) {
                list.add(recursiveParse(element, constructorsMap));
            }

            return list;
        }
        if (data.startsWith("<Integer>")) {
            return Integer.parseInt(data.substring(9));
        }
        if (data.startsWith("<Boolean>")) {
            return Boolean.parseBoolean(data.substring(9));
        }
        if (data.startsWith("<Model/")) {
            String constructorName = data.split("<Model/")[1].split(">")[0];
            Function createModel = constructorsMap.get(constructorName);

            String clearData = data.substring(8 + constructorName.length(), data.length() - 9 - constructorName.length());

            int insideModelCounter = 0;
            StringBuilder modifiedData = new StringBuilder();

            int i = 0;
            for (Character c: clearData.toCharArray()) {
                if (insideModelCounter > 0 && c == ';') {
                    modifiedData.append("_");
                } else {
                    modifiedData.append(c);
                }

                if (i + 7 < clearData.length() && clearData.substring(i, i+7).equals("<Model/")) {
                    insideModelCounter++;
                }
                if (i + 8 < clearData.length() && clearData.substring(i, i+8).equals("</Model/")) {
                    insideModelCounter--;
                }
                i++;
            }

            clearData = modifiedData.toString();

            String[] entries = clearData.split(";");
            List<String> entriesList = Arrays.stream(entries).map(entry -> entry.replace("_", ";")).toList();

            Map params = new HashMap();

            for (String entry: entriesList) {
                if (!entry.contains("=")) {
                    continue;
                }
                String key = entry.split("=")[0];
                var value = recursiveParse(entry.split("=", 2)[1], constructorsMap);
                params.put(key, value);
            }

            Object model = createModel.apply(params);
            return model;
        }

        return data;
    }
}
