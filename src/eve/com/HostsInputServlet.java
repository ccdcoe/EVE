package eve.com;

import eve.ctrl.Control;
import eve.model.infra.Element;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by jesus on 11/21/16.
 */
@WebServlet(urlPatterns = {"/hosts"})
public class HostsInputServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {

            // Read input
            String input = "";
            String line;
            while ((line = req.getReader().readLine()) != null) {
                input += line;
            }

            // Process it
            List<Element> elements = Element.getFromJSON(input);
            Control.get().newElements(elements);

        } catch (Throwable e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(e);

        }
    }
}
