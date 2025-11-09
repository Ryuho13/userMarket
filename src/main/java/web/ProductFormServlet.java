package web;

import dao.AreaDAO;
import dao.CategoryDAO;
import model.Category;
import model.SidoArea;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/product/product_form")
public class ProductFormServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("loginUserId");

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }

        try {
            CategoryDAO categoryDAO = new CategoryDAO();
            AreaDAO areaDAO = new AreaDAO();

            // ✅ DB에서 카테고리 전체 목록
            List<Category> categoryList = categoryDAO.findAll();

            // ✅ 시도 전체 목록
            List<SidoArea> sidoList = areaDAO.getAllSidos();

            // ✅ JSP로 전달
            req.setAttribute("categoryList", categoryList);
            req.setAttribute("sidoList", sidoList);

            RequestDispatcher rd = req.getRequestDispatcher("/product/product_form.jsp");
            rd.forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("상품 등록 폼 데이터 불러오기 오류", e);
        }
    }
}
