package controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.*;

@WebServlet("/chatRoom")
public class ChatRoomServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String pId = request.getParameter("productId");
        String bId = request.getParameter("buyerId");

        if (pId == null || bId == null || pId.isEmpty() || bId.isEmpty()) {
            response.getWriter().println("<h3 style='color:red;'>상품 또는 사용자 정보가 없습니다.</h3>");
            return;
        }

        long productId = Long.parseLong(pId);
        long buyerId   = Long.parseLong(bId);

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            response.getWriter().println("<h3 style='color:red;'>DB 연결 실패</h3>");
            return;
        }

        ChatDAO dao = new ChatDAO(conn);
        ChatRoom room = dao.findOrCreateRoom(productId, buyerId);
        if (room == null) {
            response.getWriter().println("<h3 style='color:red;'>채팅방 생성 실패</h3>");
            return;
        }

        List<Message> messages = dao.getMessages(room.getId());

        request.setAttribute("room", room);
        request.setAttribute("messages", messages);

        request.getRequestDispatcher("/chat/chatRoom.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }
}
