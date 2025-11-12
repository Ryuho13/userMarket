package web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/rating/form")
public class RatingFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Integer loginUserId = (session != null)
                ? (Integer) session.getAttribute("loginUserId")
                : null;

        if (loginUserId == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }

        String productIdParam = req.getParameter("productId");
        if (productIdParam == null || productIdParam.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "상품 ID가 없습니다.");
            return;
        }

        req.setAttribute("productId", productIdParam);

        req.getRequestDispatcher("/rating/rating_form.jsp")
           .forward(req, resp);
    }
}
