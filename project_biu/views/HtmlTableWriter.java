package views;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class HtmlTableWriter {

    public static ArrayList<String> getTableHtml(Map<String, String> topicMap) throws IOException {
        // Read the template HTML file
        String htmlContent = new String(Files.readAllBytes(Paths.get("html_files/table.html")));

        // Generate the table rows
        StringBuilder tableRows = new StringBuilder();
        if (topicMap != null && !topicMap.isEmpty()) {
            for (Map.Entry<String, String> entry : topicMap.entrySet()) {
                tableRows.append("<tr><td>").append(entry.getKey()).append("</td><td>").append(entry.getValue()).append("</td></tr>\n");
            }
            String placeholder = "            <tr id=\"null-data\">\n" +
                    "                <td colspan=\"2\" class=\"no-data\">Add Topic</td>\n" +
                    "            </tr>";
            htmlContent = htmlContent.replace(placeholder, tableRows.toString());
        }


        // Convert the htmlContent to an ArrayList of Strings
        ArrayList<String> htmlLines = new ArrayList<>();
        for (String line : htmlContent.split("\n")) {
            htmlLines.add(line);
        }

        return htmlLines;
    }
}
