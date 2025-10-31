package web;

import java.io.IOException;
import java.sql.SQLException;

import dao.UserDAO;
import model.User;
import model.UserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/user/addUser")
public class AddUserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/user/addUser.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String accountId = req.getParameter("account_id");
        String pw        = req.getParameter("pw");
        String name      = req.getParameter("name");
        String nickname  = req.getParameter("nickname");
        String em        = req.getParameter("em");
        String phn       = req.getParameter("phn");
        String regionId  = req.getParameter("region_id");
        String addrDet   = req.getParameter("addr_detail");

        // 회원가입 양식 한개라도 입력 안하면 실패
        if (accountId == null || accountId.isBlank()
                || pw == null || pw.isBlank()
                || name == null || name.isBlank()
                || nickname == null || nickname.isBlank()) {
            req.setAttribute("error", "필수 항목을 모두 입력하세요.");
            req.getRequestDispatcher("/user/addUser.jsp").forward(req, resp);
            return;
        }

        try {
            // 중복 체크
            if (userDAO.isAccountIdDuplicated(accountId)) {
                req.setAttribute("error", "이미 사용 중인 아이디입니다.");
                req.getRequestDispatcher("/user/addUser.jsp").forward(req, resp);
                return;
            }
            if (userDAO.isNicknameDuplicated(nickname)) {
                req.setAttribute("error", "이미 사용 중인 닉네임입니다.");
                req.getRequestDispatcher("/user/addUser.jsp").forward(req, resp);
                return;
            }

            // DTO
            User u = new User();
            u.setAccountId(accountId);
            u.setName(name);
            u.setEm(em);
            u.setPhn(phn);

            UserInfo ui = new UserInfo();
            ui.setNickname(nickname);
            ui.setAddrDetail(addrDet);
            ui.setProfileImg(null); // 초기 null
            if (regionId != null && !regionId.isBlank()) {
                try {
                    ui.setRegionId(Integer.parseInt(regionId));
                } catch (NumberFormatException ignore) {
                    ui.setRegionId(null);
                }
            }

            // 트랜잭션 INSERT
            int newUserId = userDAO.createUserWithInfo(u, ui);

            req.setAttribute("ok", "회원가입 완료! (user_id=" + newUserId + ")");
            req.getRequestDispatcher("/user/addUser.jsp").forward(req, resp);

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            req.getRequestDispatcher("/user/addUser.jsp").forward(req, resp);
        }
    }
}
