package web;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Product;

import java.io.IOException;
import java.util.*;

import dao.ProductDAO;

@WebServlet("/product/search")
public class ProductSearchServlet extends HttpServlet {
    private final ProductDAO dao = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String q = req.getParameter("q");
        String sigg = req.getParameter("sigg_area");
        String category = req.getParameter("category");

        try {
            List<Product> results = dao.searchProducts(q, sigg, category);
            req.setAttribute("products", results);
            req.setAttribute("query", q);
            req.setAttribute("sigg", sigg);
            req.setAttribute("category", category);

            RequestDispatcher rd = req.getRequestDispatcher("/product/product_search.jsp");
            rd.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("검색 중 오류 발생", e);
        }
    }
}