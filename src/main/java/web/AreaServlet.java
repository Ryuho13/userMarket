package web;

import dao.AreaDAO;
import model.SiggArea;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import com.google.gson.Gson; // ✅ Gson으로 변경

@WebServlet("/area/sigg")
public class AreaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json; charset=UTF-8");
        String sidoIdStr = req.getParameter("sidoId");

        if (sidoIdStr == null || sidoIdStr.isEmpty()) {
            resp.getWriter().write("[]");
            return;
        }

        try {
            int sidoId = Integer.parseInt(sidoIdStr);
            AreaDAO dao = new AreaDAO();
            List<SiggArea> siggs = dao.getSiggsBySido(sidoId);

            // ✅ Gson 사용
            String json = new Gson().toJson(siggs);
            resp.getWriter().write(json);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
