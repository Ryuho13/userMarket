package com.kdhcode.usermarket.servlet;

import com.kdhcode.usermarket.dao.ProductDetailDAO;
import com.kdhcode.usermarket.model.ProductDetail;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/product/detail")
public class ProductDetailServlet extends HttpServlet {

    private final ProductDetailDAO dao = new ProductDetailDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing id");
            return;
        }

        int id = 0;
        try { id = Integer.parseInt(idParam); } catch (NumberFormatException e) { }
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

            // normalize image paths for view (same logic used for list)
            for (int i = 0; i < pd.getImages().size(); i++) {
                String img = pd.getImages().get(i);
                String imgSrc = req.getContextPath() + "/resources/images/noimage.jpg";
                if (img != null && !img.trim().isEmpty()) {
                    String imgTrim = img.trim();
                    if (imgTrim.matches("(?i)^https?://.*") || imgTrim.startsWith("/")) {
                        imgSrc = imgTrim;
                    } else {
                        if (imgTrim.startsWith("../") || imgTrim.contains("resources/") || imgTrim.contains("/")) {
                            imgSrc = imgTrim;
                        } else {
                            imgSrc = req.getContextPath() + "/resources/images/" + imgTrim;
                        }
                    }
                }
                pd.getImages().set(i, imgSrc);
            }

            req.setAttribute("product", pd);
            req.getRequestDispatcher("/product/product.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
