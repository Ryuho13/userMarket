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

        // ✅ 검색 조건 받기
        String q = req.getParameter("q");
        String sigg = req.getParameter("sigg_area");
        String category = req.getParameter("category");

        try {
            ProductDAO dao = new ProductDAO();
            List<Product> products = dao.searchProducts(q, sigg, category);

            // ✅ context path 보정 (이미지 경로 prefix)
            String contextPath = req.getContextPath();
            for (Product p : products) {
                if (p.getDisplayImg() != null && !p.getDisplayImg().startsWith("http")) {
                    p.setDisplayImg(contextPath + p.getDisplayImg());
                }
            }

            // ✅ JSP에서 사용할 데이터 전달
            req.setAttribute("products", products);
            req.setAttribute("query", q);      // 👉 JSP에서 ${query} 로 접근
            req.setAttribute("sigg", sigg);
            req.setAttribute("category", category);

            // ✅ 검색 결과 페이지로 이동
            req.getRequestDispatcher("/product/product_search.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("상품 검색 실패", e);
        }
    }
}
