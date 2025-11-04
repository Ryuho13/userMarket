package web;

import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Product;

import java.io.IOException;
import java.util.List;

@WebServlet("/product/list")
public class ProductListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int page = 1;
        int size = 12;

        try {
            if (req.getParameter("page") != null)
                page = Math.max(1, Integer.parseInt(req.getParameter("page")));
        } catch (Exception ignored) {}

        int offset = (page - 1) * size;

        try {
            ProductDAO dao = new ProductDAO();
            List<Product> products = dao.listProducts(offset, size);
            int totalCount = dao.countProducts();
            int totalPages = (int) Math.ceil(totalCount / (double) size);

            // ✅ context path 붙이기
            String contextPath = req.getContextPath();
            for (Product p : products) {
                if (p.getDisplayImg() != null && !p.getDisplayImg().startsWith("http")) {
                    p.setDisplayImg(contextPath + p.getDisplayImg());
                }
            }

            req.setAttribute("products", products);
            req.setAttribute("page", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("totalCount", totalCount);

            req.getRequestDispatcher("/product/product_list.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("상품 목록 불러오기 실패", e);
        }
    }
}
