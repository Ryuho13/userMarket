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

@WebServlet("/product/list")
public class ProductListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int page = 1;
        int size = 20;

        try {
            // ✅ 페이지 파라미터 처리
            String pageParam = req.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Math.max(1, Integer.parseInt(pageParam));
            }
        } catch (NumberFormatException ignored) {}

        int offset = (page - 1) * size;

        try {
            // ✅ DAO 준비
            ProductDAO productDAO = new ProductDAO();
            AreaDAO areaDAO = new AreaDAO();
            CategoryDAO categoryDAO = new CategoryDAO();

            // ✅ 필터 파라미터 받기
            String category = req.getParameter("category");
            String region = req.getParameter("sigg_area"); // 시군구 이름 기준
            String minPriceParam = req.getParameter("minPrice");
            String maxPriceParam = req.getParameter("maxPrice");

            Integer minPrice = (minPriceParam != null && !minPriceParam.isEmpty()) ? Integer.parseInt(minPriceParam) : null;
            Integer maxPrice = (maxPriceParam != null && !maxPriceParam.isEmpty()) ? Integer.parseInt(maxPriceParam) : null;

            // ✅ 상품 목록 조회 (가격 + 카테고리 + 지역 필터 적용)
            List<Product> products = productDAO.getFilteredProducts(category, region, minPrice, maxPrice, offset, size);
            int totalCount = productDAO.countFilteredProducts(category, region, minPrice, maxPrice);
            int totalPages = (int) Math.ceil(totalCount / (double) size);

            // ✅ 이미지 경로 수정
            String contextPath = req.getContextPath();
            for (Product p : products) {
                String img = p.getDisplayImg();
                if (img != null && !img.startsWith("http")) {
                    p.setDisplayImg(contextPath + img);
                }
            }

            // ✅ 필터용 데이터
            List<SidoArea> sidoList = areaDAO.getAllSidoAreas();
            List<SiggArea> siggList = areaDAO.getAllSiggAreas();
            List<Category> categoryList = categoryDAO.getAllCategories();

            // ✅ JSP 전달 데이터 설정
            req.setAttribute("products", products);
            req.setAttribute("page", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("totalCount", totalCount);

            req.setAttribute("userSidos", sidoList);
            req.setAttribute("userSiggs", siggList);
            req.setAttribute("categories", categoryList);

            req.setAttribute("selectedCategory", category);
            req.setAttribute("selectedRegion", region);
            req.setAttribute("minPrice", minPrice);
            req.setAttribute("maxPrice", maxPrice);

            // ✅ JSP로 포워딩
            req.getRequestDispatcher("/product/product_list.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("상품 목록 불러오기 실패", e);
        }
    }
}
