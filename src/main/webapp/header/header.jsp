<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" />
    <%-- Assume header.css is relative to the current JSP location --%>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/user/css/header.css">
    <title>Header</title>
</head>
<header class="header-container">
    <div class="header-content">
        <div class="site-logo">
        	<img class="gam" alt="" src="${pageContext.request.contextPath}/user/img/gampic.png">
            <%-- 기본 상품 목록 페이지로 연결 --%>
            <a href="${pageContext.request.contextPath}/product/list">
                단감나라
            </a>
        </div>

        <div class="search-area">
            <%-- 상품 검색 서블릿 경로 (예: /product) --%>
            <form action="${pageContext.request.contextPath}/product" method="get">
                <input type="text" 
                       name="q" 
                       placeholder="상품명 또는 카테고리 검색" 
                       class="search-input"
                       value="<c:out value="${param.q}"/>">
                <button type="submit" class="search-button">
                    <i class="fa fa-search"></i>
                </button>
            </form>
        </div>

        <nav class="user-menu">
            <%-- ${sessionScope.member} 대신 ${sessionScope.loginUser}로 변경 (LoginServlet의 세션 속성명 사용) --%>
            <c:choose>
                <c:when test="${not empty sessionScope.loginUser}">
                    <%-- 로그인 후: 마이페이지(서블릿 경로)와 로그아웃(서블릿 경로) --%>
                    <a href="${pageContext.request.contextPath}/user/myPage" class="menu-item menu-mypage">마이페이지</a>
                    <a href="${pageContext.request.contextPath}/user/logout" class="menu-item menu-logout">로그아웃</a>
                </c:when>
                <c:otherwise>
                    <%-- 로그인 전: 로그인(서블릿 경로)과 회원가입(JSP) --%>
                    <a href="${pageContext.request.contextPath}/user/login" class="menu-item menu-login">로그인</a>
                    <span class="menu-divider">/</span> 
                    <a href="${pageContext.request.contextPath}/user/addUser.jsp" class="menu-item menu-signup">회원가입</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>