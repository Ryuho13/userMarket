<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>상품 목록</title>
<link rel="stylesheet" href="../resources/css/bootstrap.min.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined" />
<style>
  body {
    background-color: #f8f9fa;
    font-family: 'Noto Sans KR', sans-serif;
    margin: 0;
    padding: 0;
  }

  /* 상단 검색 영역 */
  .select_container {
    background: #fff;
    border-radius: 16px;
    margin: 20px auto 10px;
    width: 90%;
    max-width: 1500px;
    box-shadow: 0 2px 6px rgba(0,0,0,0.05);
    padding: 15px 25px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .btn-light {
    border-radius: 30px;
    border: 1px solid #dee2e6;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  }

  /* 메인 영역 레이아웃 */
  .main_container {
    display: flex;
    justify-content: center;
    gap: 3rem;
    max-width: 1600px;
    margin: 0 auto;
    padding: 20px;
  }

  /* 필터 영역 (왼쪽) */
  .product_filter {
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 2px 6px rgba(0,0,0,0.05);
    width: 400px;
    padding: 20px;
    position: sticky;
    top: 100px;
    align-self: flex-start;
  }

  /* 상품 리스트 (오른쪽) */
  	.product_items {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;    
  align-content: flex-start;
  gap: 1.5rem;
  flex-grow: 0;               
  margin: 0 auto;             
  width: 100%;                
  max-width: 1400px;          
}

  /* 상품 카드 */
  .product_item {
    border: none;
    width: 250px;
    min-width: 230px;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    cursor: pointer;
  }

	
	
	
  .product_item:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 12px rgba(0,0,0,0.1);
  }

  .product_img {
    height: 200px;
    object-fit: cover;
    border-radius: 10px;
  }

  .form-check-input:checked {
    background-color: #0d6efd;
    border-color: #0d6efd;
  }

  .pagination {
    margin-top: 30px;
    justify-content: center;
  }

  @media (max-width: 992px) {
    .main_container {
      flex-direction: column;
      align-items: center;
    }
    .product_filter {
      position: static;
      width: 100%;
      margin-bottom: 20px;
    }
  }
</style>
</head>
<body>
<%@ include file="../resources/mysql/dbconn.jsp" %>

<!-- 🔍 검색 영역 -->
<div class="select_container">
  <div class="d-flex align-items-center gap-2">
    <button type="button" class="btn btn-light d-flex align-items-center gap-1 px-3 py-2">
      <span class="material-symbols-outlined">location_on</span>
      <span class="fs-6">지역 선택</span>
    </button>
  </div>
  <div class="d-flex flex-column flex-grow-1 ms-3">
    <div class="input-group">
      <span class="input-group-text bg-white border-end-0">
        <span class="material-symbols-outlined">search</span>
      </span>
      <input type="text" class="form-control border-start-0" placeholder="상품명 또는 카테고리 검색">
      <button class="btn btn-primary">
        <span class="material-symbols-outlined">arrow_circle_right</span>
      </button>
    </div>
    <div class="mt-2 text-secondary" style="font-size:0.85rem;">인기 검색어: 노트북, 자전거, 의자, 아이폰 ...</div>
  </div>
</div>

<!-- 🧭 본문 영역 -->
<div class="main_container">

  <!-- 왼쪽 필터 -->
  <aside class="product_filter">
    <h5 class="fw-bold mb-3">필터</h5>
    <div class="form-check mb-3">
      <input class="form-check-input" type="checkbox" id="tradeOnly">
      <label class="form-check-label" for="tradeOnly">거래 기능만 보기</label>
    </div>
    <hr>

    <!-- 위치 필터 -->
    <div class="mb-4">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h6 class="fw-bold mb-0">위치</h6>
        <a href="./product_list.jsp" class="text-decoration-none text-primary small">초기화</a>
      </div>
      <div class="d-flex flex-column">
        <% 
        try {
            String sql = "SELECT s.id AS sido_id, s.name AS sido_name FROM users u JOIN activity_areas aa ON u.id = aa.user_id JOIN sigg_areas sa ON aa.id2 = sa.id JOIN sido_areas s ON sa.sido_area_id = s.id WHERE u.id = 5";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) { 
                String sidoName = rs.getString("sido_name");
        %>
        <p class="mb-1 text-secondary"><%= sidoName %></p>
        <% } } catch (SQLException e) { out.println("SQLException: " + e.getMessage()); } %>

        <% 
        try {
            String sql = "SELECT sa.id AS sigg_id, sa.name AS sigg_name FROM sigg_areas sa JOIN sido_areas s ON sa.sido_area_id = s.id WHERE s.id = (SELECT s_inner.id FROM users u JOIN activity_areas aa ON u.id = aa.user_id JOIN sigg_areas sa_inner ON aa.id2 = sa_inner.id JOIN sido_areas s_inner ON sa_inner.sido_area_id = s_inner.id WHERE u.id = 5)";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int siggId = rs.getInt("sigg_id");       
                String siggName = rs.getString("sigg_name"); 
        %>
        <div class="form-check mb-1">
          <input class="form-check-input" type="radio" name="sigg_area" id="sigg_<%=siggId%>" value="<%=siggName%>">
          <label class="form-check-label" for="sigg_<%=siggId%>"><%=siggName%></label>
        </div>
        <% } } catch (SQLException e) { out.println("SQLException: " + e.getMessage()); } %>
      </div>
    </div>

    <!-- 카테고리 필터 -->
    <div class="categories">
      <h6 class="fw-bold mb-2">카테고리</h6>
      <div class="d-flex flex-column">
        <% 
        try {
            String sql = "SELECT id, name FROM categories";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int catId = rs.getInt("id");
                String catName = rs.getString("name");
        %>
        <div class="form-check mb-1">
          <input class="form-check-input" type="radio" name="category" id="cat_<%=catId%>" value="<%=catName%>">
          <label class="form-check-label" for="cat_<%=catId%>"><%=catName%></label>
        </div>
        <% } } catch (SQLException e) { out.println("SQLException: " + e.getMessage()); } %>
      </div>
    </div>
  </aside>

  <!-- 오른쪽 상품 목록 -->
  <section class="product_items">
    <%
    try {
        String sql = "SELECT p.id AS product_id, p.title AS product_name, p.status, p.sell_price, sa.name AS sigg_name, MIN(i.name) AS img_name FROM products p JOIN products_images pi ON p.id = pi.products_id JOIN imgs i ON pi.img_id = i.id JOIN users u ON p.seller_id = u.id JOIN activity_areas aa ON u.id = aa.user_id JOIN sigg_areas sa ON aa.id2 = sa.id JOIN sido_areas s ON sa.sido_area_id = s.id GROUP BY p.id, p.title, p.status, p.sell_price, sa.name ORDER BY p.created_at DESC";
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();
        while (rs.next()) {
        	String productId = rs.getString("product_id");
            String productName = rs.getString("product_name");
            int price = rs.getInt("sell_price");
            String siggName = rs.getString("sigg_name");
            String imgName = rs.getString("img_name");
            String imgSrc = (imgName != null && !imgName.isEmpty()) ? imgName : "../resources/images/noimage.jpg";
    %>
    <a href="./product.jsp?id=<%= productId %>" type="button">
	    <div class="product_item card p-2">
	      <img src="<%= imgSrc %>" class="card-img-top rounded-4 product_img" alt="상품 이미지">
	      <div class="card-body p-2">
	        <h6 class="card-title text-truncate mb-1 fw-bold"><%= productName %></h6>
	        <p class="mb-1 text-primary fw-semibold" style="font-size:0.9rem;"><%= price %>원</p>
	        <p class="text-muted small mb-0"><%= siggName %></p>
	      </div>
	    </div>
    </a>
    <% } } catch (SQLException e) { out.println("SQLException: " + e.getMessage()); } finally { if (rs != null) rs.close(); if (pstmt != null) pstmt.close(); if (conn != null) conn.close(); } %>
  </section>
</div>

<!-- 페이지네이션 -->
<nav class="pagination mt-4">
  <ul class="pagination">
    <li class="page-item disabled"><a class="page-link" href="#">이전</a></li>
    <li class="page-item active"><a class="page-link" href="#">1</a></li>
    <li class="page-item"><a class="page-link" href="#">2</a></li>
    <li class="page-item"><a class="page-link" href="#">3</a></li>
    <li class="page-item"><a class="page-link" href="#">다음</a></li>
  </ul>
</nav>

</body>
</html>
