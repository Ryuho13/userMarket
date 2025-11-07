package web;

import java.io.IOException;
import java.util.List;
import java.sql.*; // ← JDBC 타입 인식용

import dao.DBUtil;
import dao.ProductDAO;
import dao.ProductDetailDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Product;
import model.ProductDetail;

@WebServlet("/product/detail")
public class ProductDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ProductDetailDAO detailDao = new ProductDetailDAO();
    private final ProductDAO productDao = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing id");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid id format");
            return;
        }

        if (id <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid id value");
            return;
        }

        try {
            // ✅ 상품 상세 조회 (지역 + 판매자 포함)
            ProductDetail pd = detailDao.findById(id);
            if (pd == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
                return;
            }

            // ✅ 이미지 경로 정리
            for (int i = 0; i < pd.getImages().size(); i++) {
                String img = pd.getImages().get(i);
                if (img != null && !img.isBlank()) {
                    if (!img.startsWith("http")) {
                        pd.getImages().set(i, req.getContextPath() + "/upload/product_images/" + img);
                    }
                } else {
                    pd.getImages().set(i, req.getContextPath() + "/resources/images/noimage.jpg");
                }
            }

            // ✅ 같은 카테고리 & 같은 판매자 상품 조회
            List<Product> sameCategory = productDao.getProductsByCategory(pd.getCategoryId(), pd.getId());
            List<Product> sameSeller = productDao.getProductsBySeller(pd.getSellerId(), pd.getId());

            // ✅ JSP로 전달
            req.setAttribute("product", pd);
            req.setAttribute("sameCategory", sameCategory);
            req.setAttribute("sameSeller", sameSeller);

            req.getRequestDispatcher("/product/product_detail.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("상품 상세 조회 중 오류 발생", e);
        }
    }
}
