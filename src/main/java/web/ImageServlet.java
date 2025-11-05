package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;

@WebServlet(name = "ImageServlet", urlPatterns = {"/upload/product_images/*"})
public class ImageServlet extends HttpServlet {

    private static final String BASE_PATH = "D:/upload/product_images";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
        String pathInfo = req.getPathInfo(); 
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "파일 이름이 없습니다.");
            return;
        }

        String fileName = pathInfo.substring(1); 

        File file = new File(BASE_PATH, fileName);
        System.out.println("📂 요청 파일 절대 경로: " + file.getAbsolutePath());

        if (!file.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "이미지를 찾을 수 없습니다.");
            return;
        }

        resp.setContentType(getServletContext().getMimeType(file.getName()));
        resp.setHeader("Content-Length", String.valueOf(file.length()));

        try (InputStream in = new FileInputStream(file);
             OutputStream out = resp.getOutputStream()) {
            in.transferTo(out);
        }
    }
}
