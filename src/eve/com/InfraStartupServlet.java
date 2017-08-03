package eve.com;

import eve.ctrl.Control;
import eve.ctrl.Generator;
import eve.model.event.Event;
import sun.nio.cs.Surrogate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by jesus on 11/21/16.
 */
@WebServlet(urlPatterns = {"/infraStart"})
public class InfraStartupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Generator.startup() ;
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e);

        }
    }

}
