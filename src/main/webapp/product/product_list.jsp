<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 목록</title>
<link rel="stylesheet" href="../resources/css/bootstrap.min.css">
</head>
<body>
<%@ include file="../resources/mysql/dbconn.jsp" %>

<div class="container py-4 d-flex" style="width:1600px;">
	<div class="product_items d-flex flex-wrap align-items-center border" style="width:1200px; max-width:1200px;">
	<%
	try {
	    String sql = 
	        "SELECT p.id AS product_id, p.title AS product_name, p.status, p.sell_price, " +
	        "sa.name AS sigg_name, MIN(i.name) AS img_name " +
	        "FROM products p " +
	        "JOIN products_images pi ON p.id = pi.products_id " +
	        "JOIN imgs i ON pi.img_id = i.id " +
	        "JOIN users u ON p.seller_id = u.id " +
	        "JOIN activity_areas aa ON u.id = aa.user_id " +
	        "JOIN sigg_areas sa ON aa.id2 = sa.id " +
	        "JOIN sido_areas s ON sa.sido_area_id = s.id " +
	        "GROUP BY p.id, p.title, p.status, p.sell_price, sa.name " +
	        "ORDER BY p.created_at DESC";
	
	    pstmt = conn.prepareStatement(sql);
	    rs = pstmt.executeQuery();
	
	    while (rs.next()) {
	        String productName = rs.getString("product_name");
	        int price = rs.getInt("sell_price");
	        String siggName = rs.getString("sigg_name");
	        String imgName = rs.getString("img_name");
	
	        String imgSrc = "../resources/images/noimage.jpg"; 
	        if (imgName != null && !imgName.isEmpty()) {
	            imgSrc = imgName;
	        }
	%>    
        <div class="product_item m-1 border rounded-4 shadow-sm p-3" style="width:250px; min-width:140px; height: 300px;">
            <img alt="제품_이미지" src="<%= imgSrc %>" class="product_img rounded-4 w-100 h-75">
            <p class="product_name fs-6 fw-bold mt-2 mb-0"><%= productName %></p>
            <p class="product_price mt-1 mb-0 fw-bold" style="font-size:0.8rem;"><%= price %>원</p>
            <p class="product_ text-secondary mt-1 mb-0" style="font-size:0.6rem;"><%= siggName %></p>
        </div>
	<%
	    }
	} catch (SQLException e) {
	    out.println("SQLException: " + e.getMessage());
	} finally {
	    if (rs != null) rs.close();
	    if (pstmt != null) pstmt.close();
	    if (conn != null) conn.close();
	}
	%>
</div>
<div class="pagination"></div>
</div>
</body>
</html>
