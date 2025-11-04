package web;

import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Product;

import java.io.IOException;
import java.util.List;

@WebServlet("/product/search")
public class ProductSearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String q = req.getParameter("q");
        String sigg = req.getParameter("sigg_area");
        String category = req.getParameter("category");

        try {
            ProductDAO dao = new ProductDAO();
            List<Product> products = dao.searchProducts(q, sigg, category);

            // ✅ context path 보정
            String contextPath = req.getContextPath();
            for (Product p : products) {
                if (p.getDisplayImg() != null && !p.getDisplayImg().startsWith("http")) {
                    p.setDisplayImg(contextPath + p.getDisplayImg());
                }
            }

            req.setAttribute("products", products);
            req.setAttribute("searchQuery", q);
            req.setAttribute("sigg", sigg);
            req.setAttribute("category", category);

            req.getRequestDispatcher("/product/product_search.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("상품 검색 실패", e);
        }
    }
}
