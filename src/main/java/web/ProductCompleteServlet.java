package web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dao.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

@WebServlet("/product/complete")
public class ProductCompleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }

        Integer loginUserId = (Integer) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            User loginUser = (User) session.getAttribute("loginUser");
            if (loginUser != null) {
                loginUserId = loginUser.getId();
            }
        }

        if (loginUserId == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }

        String productIdParam = req.getParameter("productId");
        if (productIdParam == null || productIdParam.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "상품 ID가 없습니다.");
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(productIdParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "상품 ID 형식이 올바르지 않습니다.");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {

            String sql = """
                UPDATE products
                   SET status = 'SOLD_OUT'
                 WHERE id = ? AND seller_id = ?
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                ps.setInt(2, loginUserId);
                int updated = ps.executeUpdate();

                if (updated == 0) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "해당 상품을 거래 완료로 변경할 수 없습니다.");
                    return;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("거래 완료 처리 중 오류가 발생했습니다.", e);
        }

        resp.sendRedirect(req.getContextPath() + "/user/myPage?tab=chats");
    }
}
