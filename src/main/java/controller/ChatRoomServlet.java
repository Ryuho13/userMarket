package controller;

import java.io.IOException;
import java.sql.Connection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.ChatDAO;
import model.ChatRoom;
import model.DBConnection;

@WebServlet("/chatRoom")
public class ChatRoomServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // 파라미터 받기
        String pId = request.getParameter("productId");
        String bId = request.getParameter("buyerId");

        System.out.println("productId = " + pId);
        System.out.println("buyerId = " + bId);

        if (pId == null || bId == null || pId.isEmpty() || bId.isEmpty()) {
            response.getWriter().println("<h3 style='color:red;'>상품 또는 사용자 정보가 없습니다. (테스트용 ID 확인)</h3>");
            return;
        }

        long productId = Long.parseLong(pId);
        long buyerId = Long.parseLong(bId);

        // DB 연결
        Connection conn = DBConnection.getConnection();
        ChatDAO dao = new ChatDAO(conn);

        ChatRoom room = dao.findOrCreateRoom(productId, buyerId);

        if (room == null) {
            response.getWriter().println("<h3 style='color:red;'>채팅방 생성 오류</h3>");
            return;
        }

        request.setAttribute("room", room);
        request.getRequestDispatcher("/chat/chatRoom.jsp").forward(request, response);
    }
}
