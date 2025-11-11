package web;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.ChatDAO;
import model.ChatRoom;
import model.Message;
import dao.ProductDetailDAO;
import model.ProductDetail;
import dao.UserDAO;
import model.UserProfile;

@WebServlet("/chatRoom")
public class ChatRoomServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setupRequestResponse(request, response);

        String roomIdParam = request.getParameter("roomId");
        String currentUserIdParam = request.getParameter("currentUserId");
        String productIdParam = request.getParameter("productId");
        String buyerIdParam = request.getParameter("buyerId");

        ChatRoom room = null;
        List<Message> messages = null;
        ProductDetail product = null;

        try {
            ChatDAO chatDAO = new ChatDAO();
            ProductDetailDAO productDetailDAO = new ProductDetailDAO();

            if (roomIdParam != null && !roomIdParam.isEmpty() && currentUserIdParam != null && !currentUserIdParam.isEmpty()) {
                // Scenario 1: Viewing an existing chat room (from myPage.jsp)
                int roomId = Integer.parseInt(roomIdParam);
                int currentUserId = Integer.parseInt(currentUserIdParam);

                room = chatDAO.findChatRoomById(roomId);
                if (room == null) {
                    sendErrorResponse(response, "채팅방을 찾을 수 없습니다.");
                    return;
                }

                product = productDetailDAO.findById(room.getProductId());
                if (product == null) {
                    sendErrorResponse(response, "채팅방과 연결된 상품을 찾을 수 없습니다.");
                    return;
                }

                // Verify current user is a participant (buyer or seller)
                if (currentUserId != room.getBuyerId() && currentUserId != product.getSellerId()) {
                    sendErrorResponse(response, "채팅방 접근 권한이 없습니다.");
                    return;
                }

                messages = chatDAO.getMessages(roomId);

            } else if (productIdParam != null && !productIdParam.isEmpty() && buyerIdParam != null && !buyerIdParam.isEmpty()) {
                // Scenario 2: Initiating a new chat (from product_detail.jsp)
                int productId = Integer.parseInt(productIdParam);
                int buyerId = Integer.parseInt(buyerIdParam);

                product = productDetailDAO.findById(productId);
                if (product == null) {
                    sendErrorResponse(response, "상품을 찾을 수 없습니다.");
                    return;
                }

                // Self-chat prevention: buyer cannot chat with their own product
                if (product.getSellerId() == buyerId) {
                    sendErrorResponse(response, "자신의 상품에는 채팅할 수 없습니다.");
                    return;
                }

                room = chatDAO.findOrCreateRoom(productId, buyerId);
                if (room == null) {
                    sendErrorResponse(response, "채팅방을 생성하거나 찾는 중 오류가 발생했습니다.");
                    return;
                }

                messages = chatDAO.getMessages(room.getId());

            } else {
                sendErrorResponse(response, "유효하지 않은 요청입니다. 상품 또는 채팅방 정보가 필요합니다.");
                return;
            }

            // Determine other user's nickname and add to request
            int finalCurrentUserId = (currentUserIdParam != null) ? Integer.parseInt(currentUserIdParam) : Integer.parseInt(buyerIdParam);
            int otherUserId = (finalCurrentUserId == room.getBuyerId()) ? product.getSellerId() : room.getBuyerId();

            dao.UserDAO userDAO = new dao.UserDAO();
            model.UserProfile otherUserProfile = userDAO.findProfileByUserId(otherUserId);
            String otherUserNickname = (otherUserProfile != null) ? otherUserProfile.getNickname() : "(알 수 없음)";
            request.setAttribute("otherUserNickname", otherUserNickname);
            
            // --- 상품 정보 request에 추가 ---
            request.setAttribute("product", product);
            // --------------------------------

            // Forward to chat room JSP
            forwardToChatRoom(request, response, room, messages);

        } catch (NumberFormatException e) {
            sendErrorResponse(response, "잘못된 ID 형식입니다.");
        } catch (Exception e) {
            throw new ServletException("채팅방 처리 중 오류 발생", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // For simplicity, delegate POST requests to doGet
        doGet(request, response);
    }

    // --- Helper Methods ---

    private void setupRequestResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.getWriter().println("<h3 style='color:red; text-align: center;'>" + message + "</h3>");
    }

    private void forwardToChatRoom(HttpServletRequest request, HttpServletResponse response, ChatRoom room, List<Message> messages) throws ServletException, IOException {
        request.setAttribute("room", room);
        request.setAttribute("messages", messages);
        request.getRequestDispatcher("/chat/chatRoom.jsp").forward(request, response);
    }
}