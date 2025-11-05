package web;

import dao.ProductDetailDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.ProductDetail;
import java.io.IOException;

@WebServlet("/product/detail")
public class ProductDetailServlet extends HttpServlet {
    private final ProductDetailDAO dao = new ProductDetailDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing id");
            return;
        }

        int id;
        try { id = Integer.parseInt(idParam); } 
        catch (NumberFormatException e) { id = 0; }

        if (id <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid id");
            return;
        }

        try {
            ProductDetail pd = dao.findById(id);
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

            req.setAttribute("product", pd);
            req.getRequestDispatcher("/product/product_detail.jsp").forward(req, resp);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
