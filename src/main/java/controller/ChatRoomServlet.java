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

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String pId = request.getParameter("productId");
        String bId = request.getParameter("buyerId");

        System.out.println("[Servlet] productId = " + pId);
        System.out.println("[Servlet] buyerId = " + bId);

        if (pId == null || bId == null || pId.isEmpty() || bId.isEmpty()) {
            response.getWriter().println("<h3 style='color:red;'>상품 또는 사용자 정보가 없습니다.</h3>");
            return;
        }

        long productId = Long.parseLong(pId);
        long buyerId = Long.parseLong(bId);

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            response.getWriter().println("<h3 style='color:red;'>DB 연결 실패</h3>");
            return;
        }

        ChatDAO dao = new ChatDAO(conn);
        ChatRoom room = dao.findOrCreateRoom(productId, buyerId);

        System.out.println("[DEBUG] findOrCreateRoom() 결과: " + (room != null ? "성공" : "실패 ❌"));
        if (room == null) {
            response.getWriter().println("<h3 style='color:red;'>채팅방 생성 오류</h3>");
            return;
        }

        List<Message> messages = dao.getMessages(room.getId());
        System.out.println("[DEBUG] 불러온 메시지 수: " + (messages != null ? messages.size() : 0));

        request.setAttribute("room", room);
        request.setAttribute("messages", messages);

        request.getRequestDispatcher("/chat/chatRoom.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
