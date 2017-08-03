package eve.com;

import eve.model.event.Event;
import eve.wuti.jpa.DAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jesus on 11/24/16.
 */
@WebServlet(urlPatterns = {"/getEvents"})
public class EveOutputServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Read parameters
        Integer maxNrOfEvents;
        try {
            maxNrOfEvents = Integer.parseInt(req.getParameter("maxNrOfEvents"));
        } catch (Throwable e) {
            maxNrOfEvents = null;
        }

        Date from;
        try {
            from = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(req.getParameter("from"));
        } catch (Throwable e) {
            from = new Date(0L);
        }

        Date until;
        try {
            until = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(req.getParameter("until"));
        } catch (Throwable e) {
            until = new Date();
        }


        // Read events from database
        Map<String, Object> params = new HashMap<>();
        params.put("from", from);
        params.put("until", until);
        List<Event> events = DAO.get().getList(Event.class, "Event.findAll", params, maxNrOfEvents);


        // Print results
        if (events != null && events.size() > 0) {
            resp.setContentType("application/json");
            PrintWriter pw = resp.getWriter();

            pw.println("[");
            boolean first = true;
            for (Event event : events) {
                if (first)
                    first = false;
                else
                    pw.print(",");
                pw.print(event.toJSON());
            }
            pw.println("]");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}

