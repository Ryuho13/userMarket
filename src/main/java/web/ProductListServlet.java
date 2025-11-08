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

        // 검색/필터 파라미터
        String q            = trimToNull(req.getParameter("q"));
        String categoryParam = trimToNull(req.getParameter("category"));   // 문자열(이름 또는 ID)
        String siggParam     = trimToNull(req.getParameter("sigg_area"));  // 문자열(이름 또는 ID)

        Integer categoryId = parseIntOrNull(categoryParam); // 숫자면 ID
        Integer siggAreaId = parseIntOrNull(siggParam);     // 숫자면 ID

        Integer minPrice   = parseIntOrNull(req.getParameter("minPrice"));
        Integer maxPrice   = parseIntOrNull(req.getParameter("maxPrice"));

        try {
            ProductDAO  productDAO  = new ProductDAO();
            AreaDAO     areaDAO     = new AreaDAO();
            CategoryDAO categoryDAO = new CategoryDAO();

            // 좌측 필터용 데이터 먼저 가져옴 (이걸로 이름 매핑)
            List<SidoArea>  sidoList     = areaDAO.getAllSidoAreas();
            List<SiggArea>  siggList     = areaDAO.getAllSiggAreas();
            List<Category>  categoryList = categoryDAO.getAllCategories();

            // ▶ 실제 데이터 조회
            List<Product> products;
            int totalCount;

            // 검색어나 ID 필터가 있으면 → ID 기반 검색 API 사용
            if (q != null || categoryId != null || siggAreaId != null) {
                totalCount = productDAO.countSearchProducts(q, categoryId, siggAreaId);
                products   = productDAO.searchProducts(q, categoryId, siggAreaId, offset, size);
            } else {
                // 그 외(가격/이름 기반 필터)
                products   = productDAO.getFilteredProducts(categoryParam, siggParam, minPrice, maxPrice, offset, size);
                totalCount = productDAO.countFilteredProducts(categoryParam, siggParam, minPrice, maxPrice);
            }

            int totalPages = (int) Math.ceil(totalCount / (double) size);

            // ▶ 여기서 “선택된 필터 이름”을 계산해서 JSP에 넘겨줌
            String selectedCategoryName = null;
            if (categoryId != null) { // 숫자(ID)인 경우, 목록에서 찾아서 이름으로 변환
                for (Category c : categoryList) {
                    if (c.getId() == categoryId) {
                        selectedCategoryName = c.getName();
                        break;
                    }
                }
            } else {
                selectedCategoryName = categoryParam; // 애초에 이름으로 들어온 경우
            }

            String selectedSiggName = null;
            if (siggAreaId != null) { // 숫자(ID)
                for (SiggArea s : siggList) {
                    if (s.getId() == siggAreaId) {
                        selectedSiggName = s.getName();
                        break;
                    }
                }
            } else {
                selectedSiggName = siggParam; // 이름으로 들어온 경우
            }

            // 뷰에 전달
            req.setAttribute("products", products);
            req.setAttribute("page", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("totalCount", totalCount);

            req.setAttribute("userSidos", sidoList);
            req.setAttribute("userSiggs", siggList);
            req.setAttribute("categories", categoryList);

            // 현재 파라미터 (폼 유지)
            req.setAttribute("q", q);
            req.setAttribute("category", categoryParam);
            req.setAttribute("sigg_area", siggParam);
            req.setAttribute("minPrice", minPrice);
            req.setAttribute("maxPrice", maxPrice);

            // ✅ 필터 태그에서 쓸 “예쁘게 표시용 이름”
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
