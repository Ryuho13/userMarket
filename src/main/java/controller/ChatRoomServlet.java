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

/**
 * ChatRoomServlet
 * 상품 상세 페이지에서 "채팅하기" 버튼을 눌렀을 때 호출됨
 * 기존 채팅방을 찾거나, 없으면 새로 생성 후 chatRoom.jsp로 이동
 */
@WebServlet("/chatRoom")
public class ChatRoomServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // ✅ 파라미터 받기
        String pId = request.getParameter("productId");
        String bId = request.getParameter("buyerId");

        System.out.println("productId = " + pId);
        System.out.println("buyerId = " + bId);

        // ✅ 파라미터 검증
        if (pId == null || bId == null || pId.isEmpty() || bId.isEmpty()) {
            response.getWriter().println("<h3 style='color:red;'>상품 또는 사용자 정보가 없습니다.</h3>");
            return;
        }

        long productId = Long.parseLong(pId);
        long buyerId = Long.parseLong(bId);

        // ✅ DB 연결 및 DAO 생성
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            response.getWriter().println("<h3 style='color:red;'>DB 연결 실패</h3>");
            return;
        }

        ChatDAO dao = new ChatDAO(conn);

        // ✅ 채팅방 찾거나 새로 생성
        ChatRoom room = dao.findOrCreateRoom(productId, buyerId);
        if (room == null) {
            response.getWriter().println("<h3 style='color:red;'>채팅방 생성 오류</h3>");
            return;
        }

        // ✅ 기존 메시지 불러오기
        List<Message> messages = dao.getMessages(room.getId());

        // ✅ JSP에 전달할 데이터 저장
        request.setAttribute("room", room);
        request.setAttribute("messages", messages);

        // ✅ JSP로 안전하게 이동 (context root 자동 인식)
        String jspPath = "/chat/chatRoom.jsp";
        request.getRequestDispatcher(jspPath).forward(request, response);
    }

    // 만약 GET 요청이 들어올 경우를 대비해서 추가
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
