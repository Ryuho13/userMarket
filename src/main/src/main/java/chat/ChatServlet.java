// src/main/java/com/example/chat/ChatServlet.java
package chatBox;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet(urlPatterns = {"/chat/messages", "/chat/send"})
public class ChatServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final List<Map<String,Object>> MESSAGES = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicInteger ID_GEN = new AtomicInteger(1);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sinceParam = req.getParameter("since");
        int since = 0;
        try { if (sinceParam != null) since = Integer.parseInt(sinceParam); } catch (Exception ignored) {}

        List<Map<String,Object>> out;
        synchronized (MESSAGES) {
            out = new ArrayList<>();
            for (Map<String,Object> m : MESSAGES) {
                int id = (Integer)m.get("id");
                if (id > since) out.add(m);
            }
        }

        resp.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(resp.getWriter(), out);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String,String> body = mapper.readValue(req.getInputStream(), Map.class);
        String name = body.getOrDefault("name", "익명");
        String text = body.getOrDefault("text", "").trim();
        if (text.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Map<String,Object> msg = new HashMap<>();
        msg.put("id", ID_GEN.getAndIncrement());
        msg.put("name", name);
        msg.put("text", text);
        msg.put("ts", System.currentTimeMillis());

        MESSAGES.add(msg);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
