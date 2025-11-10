package web;

import dao.ProductDAO;
import dao.AreaDAO;
import dao.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Product;
import model.SidoArea;
import model.SiggArea;
import model.Category;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/product/list", "/product", "/product/"})
public class ProductListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        int size   = 20;
        int page   = parseIntOrDefault(req.getParameter("page"), 1);
        int offset = (page - 1) * size;

        String q            = trimToNull(req.getParameter("q"));
        String categoryParam = trimToNull(req.getParameter("category"));   
        String siggParam     = trimToNull(req.getParameter("sigg_area"));  

        Integer categoryId = parseIntOrNull(categoryParam); 
        Integer siggAreaId = parseIntOrNull(siggParam);     

        Integer minPrice   = parseIntOrNull(req.getParameter("minPrice"));
        Integer maxPrice   = parseIntOrNull(req.getParameter("maxPrice"));

        try {
            ProductDAO  productDAO  = new ProductDAO();
            AreaDAO     areaDAO     = new AreaDAO();
            CategoryDAO categoryDAO = new CategoryDAO();

            List<SidoArea>  sidoList     = areaDAO.getAllSidoAreas();
            List<SiggArea>  siggList     = areaDAO.getAllSiggAreas();
            List<Category>  categoryList = categoryDAO.getAllCategories();

            List<Product> products;
            int totalCount;

            if (q != null || categoryId != null || siggAreaId != null) {
                totalCount = productDAO.countSearchProducts(q, categoryId, siggAreaId);
                products   = productDAO.searchProducts(q, categoryId, siggAreaId, offset, size);
            } else {
                products   = productDAO.getFilteredProducts(categoryParam, siggParam, minPrice, maxPrice, offset, size);
                totalCount = productDAO.countFilteredProducts(categoryParam, siggParam, minPrice, maxPrice);
            }

            int totalPages = (int) Math.ceil(totalCount / (double) size);
            String selectedCategoryName = null;
            if (categoryId != null) { 
                for (Category c : categoryList) {
                    if (c.getId() == categoryId) {
                        selectedCategoryName = c.getName();
                        break;
                    }
                }
            } else {
                selectedCategoryName = categoryParam; 
            }

            String selectedSiggName = null;
            if (siggAreaId != null) { 
                for (SiggArea s : siggList) {
                    if (s.getId() == siggAreaId) {
                        selectedSiggName = s.getName();
                        break;
                    }
                }
            } else {
                selectedSiggName = siggParam; 
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
            req.setAttribute("minPrice", minPrice);
            req.setAttribute("maxPrice", maxPrice);
            req.setAttribute("selectedCategoryName", selectedCategoryName);
            req.setAttribute("selectedSiggName", selectedSiggName);

            req.getRequestDispatcher("/product/product_list.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("상품 목록/검색 처리 실패", e);
        }
    }

    private static String  trimToNull(String s){ if(s==null) return null; s=s.trim(); return s.isEmpty()?null:s; }
    private static Integer parseIntOrNull(String s){ try{ if(s==null||s.isBlank()) return null; return Integer.valueOf(s.trim()); }catch(Exception e){ return null; } }
    private static int     parseIntOrDefault(String s,int def){ try{ return Integer.parseInt(s); }catch(Exception e){ return def; } }
}
