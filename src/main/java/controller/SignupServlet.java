// src/main/java/controller/SignupServlet.java
package controller;

import dao.MemberDAO;
import dto.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

	private final MemberDAO memberDAO = new MemberDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String email = nvl(request.getParameter("email"));
		String password = nvl(request.getParameter("password"));
		String nickname = nvl(request.getParameter("nickname"));
		String regionCode = emptyToNull(request.getParameter("regionCode"));

		String error = validate(email, password, nickname);
		if (error != null) {
			request.setAttribute("error", error);
			request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
			return;
		}

		try {
			if (memberDAO.existsByEmail(email)) {
				request.setAttribute("error", "이미 사용 중인 이메일입니다.");
				request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
				return;
			}
			if (memberDAO.existsByNickname(nickname)) {
				request.setAttribute("error", "이미 사용 중인 닉네임입니다.");
				request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
				return;
			}

			Member m = new Member();
			m.setEmail(email);
			m.setPassword(password);
			m.setNickname(nickname);
			m.setRegionCode(regionCode);

			long memberId = memberDAO.insert(m);
			if (memberId > 0) {
				request.setAttribute("memberId", memberId);
				request.getRequestDispatcher("/WEB-INF/views/signup_success.jsp").forward(request, response);
			} else {
				request.setAttribute("error", "회원가입 중 오류가 발생했습니다.");
				request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
			}

		} catch (Exception e) {
			request.setAttribute("error", "DB 오류: " + e.getMessage());
			request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
		}
	}

	private String nvl(String s) {
		return s == null ? "" : s.trim();
	}

	private String emptyToNull(String s) {
		if (s == null)
			return null;
		s = s.trim();
		return s.isEmpty() ? null : s;
	}

	private String validate(String email, String password, String nickname) {
		if (email.isEmpty() || !email.contains("@"))
			return "이메일을 올바르게 입력하세요.";
		if (password.length() < 4)
			return "비밀번호는 4자 이상 입력하세요.";
		if (nickname.length() < 2 || nickname.length() > 40)
			return "닉네임은 2~40자여야 합니다.";
		return null;
	}
}
