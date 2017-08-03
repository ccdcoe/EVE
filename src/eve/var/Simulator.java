package eve.var;

import eve.model.event.Event;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by jesus on 11/16/16.
 */
public class Simulator {

    private Simulator() {
        simulateRestful();
    }

    private void simulateRestful() {
        new Thread() {
            @Override
            public void run() {
                try {
                    pushRestfulEvents();
                } catch (Exception e) {
                    System.out.println("Could not simulate restful -> " + e.getMessage());
                }
            }
        }.start();
    }


    public void pushRestfulEvents() throws Exception {

        String sEvents;
        try {
            sEvents = getEventsFromFile();
        } catch (Exception ex) {
            throw new Exception("Could not read events file: " + ex.getMessage());
        }


        boolean printing = false;
        PrintWriter pw = null;
        String[] lines = sEvents.split("\n");
        for (int i = 0; i < 4; i++) {
            for (String line : lines)
                if (line.trim().equals("INIT_OBSERVATION")) {
                    HttpURLConnection connection = (HttpURLConnection) new URL("http://10.224.16.26:7777/newEvent").openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    pw = new PrintWriter(connection.getOutputStream());
                    printing = true;
                } else if (line.trim().equals("END_OBSERVATION")) {
                    if (pw != null) {
                        pw.close();
                        pw.flush();
                    }
                    printing = false;
                    Thread.sleep(2000);
                } else if (printing) {
                    if (pw != null) pw.println(line);
                }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private String getEventsFromFile() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(Event.class.getClassLoader().getResource("samples").toURI()));

        StringBuilder content = new StringBuilder();
        for (String line : lines) content.append(line).append("\n");

        return content.toString();
    }


    public static void main(String[] args) {
        new Simulator();
    }
}
