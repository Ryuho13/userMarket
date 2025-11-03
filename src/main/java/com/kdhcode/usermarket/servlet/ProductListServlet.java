package com.kdhcode.usermarket.servlet;

import com.kdhcode.usermarket.dao.ProductDAO;
import com.kdhcode.usermarket.model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/product/list")
public class ProductListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = 1;
        int size = 12;

        try {
            String p = req.getParameter("page");
            if (p != null) page = Math.max(1, Integer.parseInt(p));
        } catch (Exception ignore) {}

        try {
            String s = req.getParameter("size");
            if (s != null) size = Math.max(1, Integer.parseInt(s));
        } catch (Exception ignore) {}

        int offset = (page - 1) * size;

        ProductDAO dao = new ProductDAO();

        try {
            // ✅ 상품 목록 불러오기 (디버깅 로그 포함)
            List<Product> products = dao.listProducts(offset, size);
            System.out.println("Product size = " + products.size());
            for (Product p : products) {
                System.out.println(p.getTitle() + " / " + p.getSellPrice());
            }

            // ✅ 전체 개수 계산
            int totalCount = dao.countProducts();
            int totalPages = (int) Math.ceil(totalCount / (double) size);
            if (totalPages < 1) totalPages = 1;

            // ✅ 이미지 URL 보정
            String contextPath = req.getContextPath();
            for (Product p : products) {
                String img = p.getImgName();
                String display = contextPath + "/resources/images/noimage.jpg";

                if (img != null && !img.isEmpty()) {
                    String t = img.trim();
                    if (t.matches("(?i)^https?://.*") || t.startsWith("/")) {
                        display = t;
                    } else if (t.startsWith("../")) {
                        String normalized = t.replaceFirst("^\\.\\./+", "");
                        display = contextPath + "/" + normalized;
                    } else if (t.contains("resources/") || t.contains("/")) {
                        display = t;
                    } else {
                        display = contextPath + "/resources/images/" + t;
                    }
                }
                p.setDisplayImg(display);
            }

            // ✅ 파라미터 보존용
            StringBuilder sb = new StringBuilder();
            req.getParameterMap().forEach((k, v) -> {
                if (!"page".equals(k)) {
                    for (String val : v) {
                        try {
                            sb.append("&")
                              .append(java.net.URLEncoder.encode(k, "UTF-8"))
                              .append("=")
                              .append(java.net.URLEncoder.encode(val, "UTF-8"));
                        } catch (Exception ignored) {}
                    }
                }
            });

            // ✅ JSP로 전달
            req.setAttribute("products", products);
            req.setAttribute("page", page);
            req.setAttribute("size", size);
            req.setAttribute("totalCount", totalCount);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("preserveParams", sb.toString());

            // ✅ 추가 필터 데이터
            try {
                com.kdhcode.usermarket.dao.CategoryDAO cdao = new com.kdhcode.usermarket.dao.CategoryDAO();
                req.setAttribute("categories", cdao.listCategories());
            } catch (Exception ignored) {}

            try {
                com.kdhcode.usermarket.dao.AreaDAO adao = new com.kdhcode.usermarket.dao.AreaDAO();
                Integer sessionUserId = null;
                Object uidObj = req.getSession().getAttribute("userId");
                if (uidObj != null) {
                    try { sessionUserId = Integer.parseInt(uidObj.toString()); } catch (Exception ignored) {}
                }
                if (sessionUserId != null) {
                    req.setAttribute("userSidos", adao.getUserSidos(sessionUserId));
                    req.setAttribute("userSiggs", adao.getSiggsByUser(sessionUserId));
                }
            } catch (Exception ignored) {}

            // ✅ JSP로 포워드
            req.getRequestDispatcher("/product/product_list.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}
