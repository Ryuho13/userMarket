package web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/uploadImage")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class ImageUploadServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads" + File.separator + "chat_images";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Get the real path for the upload directory
        String applicationPath = request.getServletContext().getRealPath("");
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

        // Create the upload directory if it does not exist
        File uploadDir = new File(uploadFilePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = null;
        String imageUrl = null;

        try (InputStream fileContent = request.getPart("image").getInputStream()) {
            // Generate a unique file name
            String originalFileName = Paths.get(request.getPart("image").getSubmittedFileName()).getFileName().toString();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            fileName = UUID.randomUUID().toString() + extension;
            
            // Save the file
            Files.copy(fileContent, Paths.get(uploadFilePath + File.separator + fileName), StandardCopyOption.REPLACE_EXISTING);

            // Create the relative URL for the client
            imageUrl = "/" + UPLOAD_DIR.replace(File.separator, "/") + "/" + fileName;

            response.getWriter().write(String.format("{\"success\": true, \"imageUrl\": \"%s\"}", imageUrl));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(String.format("{\"success\": false, \"error\": \"이미지 업로드 실패: %s\"}", e.getMessage()));
            e.printStackTrace();
        }
    }
}
