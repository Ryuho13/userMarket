package controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.ChatDAO;
import model.ChatRoom;
import model.Message;
import model.DBConnection;

@WebServlet("/chatRoom")
public class ChatRoomServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setupRequestResponse(request, response);

        String pId = request.getParameter("productId");
        String bId = request.getParameter("buyerId");

        if (!areParametersValid(pId, bId)) {
            sendErrorResponse(response, "상품 또는 사용자 정보가 없습니다.");
            return;
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            sendErrorResponse(response, "DB 연결에 실패했습니다.");
            return;
        }

        ChatDAO dao = new ChatDAO(conn);
        ChatRoom room = dao.findOrCreateRoom(Long.parseLong(pId), Long.parseLong(bId));

        if (room == null) {
            sendErrorResponse(response, "채팅방을 생성하거나 찾는 중 오류가 발생했습니다.");
            return;
        }

        List<Message> messages = dao.getMessages(room.getId());

        // 성공 시, 데이터를 request에 담아 JSP로 포워딩
        forwardToChatRoom(request, response, room, messages);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    // --- Helper Methods ---

    private void setupRequestResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
    }

    private boolean areParametersValid(String productId, String buyerId) {
        return productId != null && !productId.isEmpty() && buyerId != null && !buyerId.isEmpty();
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.getWriter().println("<h3 style='color:red;'>" + message + "</h3>");
    }

    private void forwardToChatRoom(HttpServletRequest request, HttpServletResponse response, ChatRoom room, List<Message> messages) throws ServletException, IOException {
        request.setAttribute("room", room);
        request.setAttribute("messages", messages);
        request.getRequestDispatcher("/chat/chatRoom.jsp").forward(request, response);
    }
}