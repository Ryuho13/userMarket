<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>상품 상세</title>
    <link rel="stylesheet" href="../resources/css/bootstrap.min.css">
</head>
<body>
<%@ include file="../resources/mysql/dbconn.jsp" %>

<%
    String idParam = request.getParameter("id");
    if (idParam == null) {
%>
    <div class="container mt-4">
        <div class="alert alert-danger">잘못된 접근입니다. 상품 ID가 필요합니다.</div>
        <a href="product_list.jsp" class="btn btn-primary">목록으로</a>
    </div>
<%
        return;
    }

    int productId = 0;
    try { productId = Integer.parseInt(idParam); } catch (NumberFormatException e) { }
    if (productId <= 0) {
%>
    <div class="container mt-4">
        <div class="alert alert-danger">유효하지 않은 상품 ID입니다.</div>
        <a href="product_list.jsp" class="btn btn-primary">목록으로</a>
    </div>
<%
        return;
    }

    java.sql.PreparedStatement ps = null;
    String title = "";
    String description = "";
    int price = 0;
    int sellerId = 0;
    String status = "";

    try {
        String sql = "SELECT id, title, description, sell_price, seller_id, status FROM products WHERE id = ?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, productId);
        rs = ps.executeQuery();
        if (rs.next()) {
            title = rs.getString("title");
            description = rs.getString("description");
            price = rs.getInt("sell_price");
            sellerId = rs.getInt("seller_id");
            status = rs.getString("status");
        } else {
%>
    <div class="container mt-4">
        <div class="alert alert-warning">찾을 수 없는 상품입니다.</div>
        <a href="product_list.jsp" class="btn btn-primary">목록으로</a>
    </div>
<%
            try { if (rs != null) rs.close(); } catch (Exception ignore) {}
            try { if (ps != null) ps.close(); } catch (Exception ignore) {}
            return;
        }
    } catch (SQLException e) {
        out.println("SQLException: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException ignore) {}
        try { if (ps != null) ps.close(); } catch (SQLException ignore) {}
    }

    java.util.List<String> images = new java.util.ArrayList<>();
    try {
        String sqlImgs = "SELECT i.name FROM imgs i JOIN products_images pi ON i.id = pi.img_id WHERE pi.products_id = ?";
        ps = conn.prepareStatement(sqlImgs);
        ps.setInt(1, productId);
        rs = ps.executeQuery();
        while (rs.next()) {
            images.add(rs.getString("name"));
        }
    } catch (SQLException e) {
        out.println("SQLException: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception ignore) {}
        try { if (ps != null) ps.close(); } catch (Exception ignore) {}
    }

    String sellerMobile = "";
    Double sellerRating = null;
    try {
        String sqlSeller = "SELECT mobile_number, rating_score FROM users WHERE id = ?";
        ps = conn.prepareStatement(sqlSeller);
        ps.setInt(1, sellerId);
        rs = ps.executeQuery();
        if (rs.next()) {
            sellerMobile = rs.getString("mobile_number");
            sellerRating = rs.getDouble("rating_score");
        }
    } catch (SQLException e) {
        out.println("SQLException: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception ignore) {}
        try { if (ps != null) ps.close(); } catch (Exception ignore) {}
    }

    String sellerSigg = "";
    try {
        String sqlLoc = "SELECT sa.name AS sigg_name FROM activity_areas aa JOIN sigg_areas sa ON aa.id2 = sa.id WHERE aa.user_id = ? LIMIT 1";
        ps = conn.prepareStatement(sqlLoc);
        ps.setInt(1, sellerId);
        rs = ps.executeQuery();
        if (rs.next()) sellerSigg = rs.getString("sigg_name");
    } catch (SQLException e) {
        out.println("SQLException: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception ignore) {}
        try { if (ps != null) ps.close(); } catch (Exception ignore) {}
    }

%>

<div class="container py-4">
    <a href="product_list.jsp" class="btn btn-secondary mb-3">목록으로</a>
    <div class="row">
        <div class="col-md-6">
            <div>
                <% if (images.isEmpty()) { %>
                    <img src="<%= request.getContextPath() %>/resources/images/noimage.jpg" class="img-fluid rounded" alt="이미지 없음">
                <% } else { %>
                    <% for (int i=0;i<images.size();i++) {
                        String img = images.get(i);
                        String imgSrc = request.getContextPath() + "/resources/images/noimage.jpg";
                        if (img != null && !img.isEmpty()) {
                            String imgTrim = img.trim();
                            if (imgTrim.matches("(?i)^https?://.*") || imgTrim.startsWith("/")) {
                                imgSrc = imgTrim;
                            } else {
                                if (imgTrim.startsWith("../") || imgTrim.contains("resources/") || imgTrim.contains("/")) {
                                    imgSrc = imgTrim;
                                } else {
                                    imgSrc = request.getContextPath() + "/resources/images/" + imgTrim;
                                }
                            }
                        }
                    %>
                        <img src="<%= imgSrc %>" class="img-fluid rounded mb-2" alt="상품 이미지">
                    <% } %>
                <% } %>
            </div>
        </div>
        <div class="col-md-6">
            <h2 class="fw-bold"><%= title %></h2>
            <p class="text-muted">지역: <%= sellerSigg %></p>
            <p class="fs-4 text-danger fw-bold"><%= price %>원</p>
            <p class="mt-3"><%= description %></p>

            <hr>
            <h5>판매자 정보</h5>
            <p>연락처: <%= sellerMobile %></p>
            <p>평점: <%= (sellerRating!=null? String.format("%.1f", sellerRating) : "-") %></p>

            <div class="mt-3">
                <a href="#" class="btn btn-primary">채팅으로 문의</a>
                <a href="#" class="btn btn-outline-secondary">찜</a>
            </div>
        </div>
    </div>
</div>

</body>
</html>