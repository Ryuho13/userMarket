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
        int size = 40;

        try {
            // ✅ 페이지 번호 파라미터 처리
            String pageParam = req.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Math.max(1, Integer.parseInt(pageParam));
            }
        } catch (NumberFormatException ignored) {}

        int offset = (page - 1) * size;

        try {
            // ✅ DAO 초기화
            ProductDAO productDAO = new ProductDAO();
            AreaDAO areaDAO = new AreaDAO();
            CategoryDAO categoryDAO = new CategoryDAO();

            // ✅ 상품 목록
            List<Product> products = productDAO.listProducts(offset, size);
            int totalCount = productDAO.countProducts();
            int totalPages = (int) Math.ceil(totalCount / (double) size);

            // ✅ 이미지 경로 보정 (상대경로 → 절대경로)
            String contextPath = req.getContextPath();
            for (Product p : products) {
                String img = p.getDisplayImg();
                if (img != null && !img.startsWith("http")) {
                    p.setDisplayImg(contextPath + img);
                }
            }

            // ✅ 필터용 데이터 로드
            List<SidoArea> sidoList = areaDAO.getAllSidoAreas();
            List<SiggArea> siggList = areaDAO.getAllSiggAreas();
            List<Category> categoryList = categoryDAO.getAllCategories();

            // ✅ JSP에 전달할 속성 설정
            req.setAttribute("products", products);
            req.setAttribute("page", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("totalCount", totalCount);

            req.setAttribute("userSidos", sidoList);
            req.setAttribute("userSiggs", siggList);
            req.setAttribute("categories", categoryList);

            // ✅ JSP로 포워딩
            req.getRequestDispatcher("/product/product_list.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("상품 목록 불러오기 실패", e);
        }
    }
}
