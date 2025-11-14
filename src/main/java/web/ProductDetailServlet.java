package web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dao.DBUtil;
import dao.ProductDAO;
import dao.ProductDetailDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

        final int id;
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
        	ProductDetail pd = detailDao.incrementAndFindById(id);
        	if (pd == null) {
        	    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
        	    return;
        	}

        	if (pd.getImages() == null || pd.getImages().isEmpty()) {
        	    pd.getImages().add(req.getContextPath() + "/product/resources/images/noimage.jpg");
        	}

        	for (int i = 0; i < pd.getImages().size(); i++) {
        	    String img = pd.getImages().get(i);

        	    if (img != null && !img.isBlank()) {
        	        if (!img.startsWith("http")) {
        	            pd.getImages().set(i,
        	                    req.getContextPath() + "/upload/product_images/" + img);
        	        }
        	    } else {
        	        pd.getImages().set(i,
        	                req.getContextPath() + "/product/resources/images/noimage.jpg");
        	    }
        	}


     
            List<Product> sameCategory =
                    productDao.getProductsByCategory(pd.getCategoryId(), pd.getId());
            List<Product> sameSeller =
                    productDao.getProductsBySeller(pd.getSellerId(), pd.getId());

         
            HttpSession session = req.getSession(false);
            Integer loginUserId = (session != null)
                    ? (Integer) session.getAttribute("loginUserId")
                    : null;

            boolean isWished = false;
            if (loginUserId != null) {
                isWished = checkWish(loginUserId, pd.getId());
            }

            
            req.setAttribute("product", pd);
            req.setAttribute("sameCategory", sameCategory);
            req.setAttribute("sameSeller", sameSeller);
            req.setAttribute("isWished", isWished);   

            req.getRequestDispatcher("/product/product_detail.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("상품 상세 조회 중 오류 발생", e);
        }
    }


    private boolean checkWish(int userId, int productId) {
        String sql = "SELECT 1 FROM wish_lists WHERE register_id = ? AND product_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);    
            ps.setInt(2, productId);  

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();      
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;          
        }
    }
}