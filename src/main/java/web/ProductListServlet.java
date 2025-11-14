package web;

import dao.ProductDAO;
import dao.AreaDAO;
import dao.CategoryDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.*;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/product/list", "/product", "/product/"})
public class ProductListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        int size = 40;
        int page = parseIntOrDefault(req.getParameter("page"), 1);
        if (page < 1) page = 1;
        int offset = (page - 1) * size;
        String q             = trimToNull(req.getParameter("q"));
        String categoryParam = trimToNull(req.getParameter("category"));
        String siggParam     = trimToNull(req.getParameter("sigg_area"));
        String sidoParam     = trimToNull(req.getParameter("sidoId"));
        Integer categoryId = parseIntOrNull(categoryParam);
        Integer siggAreaId = parseIntOrNull(siggParam);
        Integer minPrice = parseIntOrNull(req.getParameter("minPrice"));
        Integer maxPrice = parseIntOrNull(req.getParameter("maxPrice"));

        boolean onlyAvailable = parseBooleanLoose(req.getParameter("onlyAvailable")); 

        String sort = req.getParameter("sort");
        if (sort == null || sort.isBlank()) sort = "latest";

        try {
            ProductDAO productDAO   = new ProductDAO();
            AreaDAO areaDAO         = new AreaDAO();
            CategoryDAO categoryDAO = new CategoryDAO();

            List<SidoArea> sidoList     = areaDAO.getAllSidoAreas();
            List<SiggArea> siggList     = areaDAO.getAllSiggAreas();
            List<Category> categoryList = categoryDAO.getAllCategories();

            int totalCount = productDAO.countSearchProducts(
                    q, categoryId, siggAreaId, minPrice, maxPrice, onlyAvailable
            );

            List<Product> products = productDAO.searchProducts(
                    q, categoryId, siggAreaId, minPrice, maxPrice,
                    offset, size, sort, onlyAvailable
            );

            int totalPages = (int) Math.ceil(totalCount / (double) size);

            if (categoryId != null) {
                Category selectedCategory = categoryDAO.getCategoryById(categoryId);
                if (selectedCategory != null) {
                    req.setAttribute("selectedCategoryName", selectedCategory.getName());
                }
            }
            if (siggAreaId != null) {
                SiggArea selectedSigg = areaDAO.getSiggAreaById(siggAreaId);
                if (selectedSigg != null) {
                    req.setAttribute("selectedSiggName", selectedSigg.getName());
                }
            }

            req.setAttribute("products", products);
            req.setAttribute("page", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("totalCount", totalCount);
            req.setAttribute("userSidos", sidoList);
            req.setAttribute("userSiggs", siggList);
            req.setAttribute("categories", categoryList);
            req.setAttribute("q", q);
            req.setAttribute("category", categoryParam);
            req.setAttribute("sigg_area", siggParam);
            req.setAttribute("sidoId", sidoParam);
            req.setAttribute("minPrice", minPrice);
            req.setAttribute("maxPrice", maxPrice);
            req.setAttribute("sort", sort);
            req.setAttribute("onlyAvailable", onlyAvailable); 

            req.getRequestDispatcher("/product/product_list.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("상품 목록/검색 처리 실패", e);
        }
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private static Integer parseIntOrNull(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return Integer.valueOf(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static int parseIntOrDefault(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private static boolean parseBooleanLoose(String s) {
        if (s == null) return false;
        String v = s.trim().toLowerCase();
        return v.equals("1") || v.equals("true") || v.equals("on") || v.equals("y") || v.equals("yes");
    }
}
