<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">

<header class="header-container">
    <div class="header-content">
        <div class="site-logo">
            <a href="${pageContext.request.contextPath}/index.jsp">
                단감나라
            </a>
        </div>

        <div class="search-area">
            <form action="search.do" method="get">
                <input type="text" name="query" placeholder="동네 생활, 상품 검색" class="search-input">
                <button type="submit" class="search-button">
                    <i class="fa fa-search"></i> </button>
            </form>
        </div>

        <nav class="user-menu">
            <c:choose>
                <c:when test="${not empty sessionScope.member}">
                    <a href="${pageContext.request.contextPath}/member/myPage.jsp" class="menu-item menu-mypage">마이페이지</a>
                    <a href="${pageContext.request.contextPath}/member/logout.do" class="menu-item menu-logout">로그아웃</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/member/loginMember.jsp" class="menu-item menu-login">로그인</a>
                    <span class="menu-divider">/</span> 
                    <a href="${pageContext.request.contextPath}/member/addMember.jsp" class="menu-item menu-signup">회원가입</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>