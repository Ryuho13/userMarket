<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- 
    요청에 따라, 모든 내부 스타일(<style> 블록)을 제거하고 
    외부 CSS 파일인 header.css를 참조하는 <link> 태그를 사용하도록 수정했습니다.
    이 <link> 태그는 이 JSP를 포함하는 최종 HTML 문서의 <head> 태그 내에 위치해야 정상 작동합니다.
--%>
<head>
<style type="text/css">

    /* [수정] 상단 여백 제거를 위한 전역 리셋 */
    html, body {
        margin: 0;
        padding: 0;
    }

    /* 기본 컨테이너 (화면 전체 너비 100%) */
    .header-container {
        width: 100%;
        background-color: #ffffff; /* 하얀 배경 */
        border-bottom: 1px solid #e5e7eb; /* 얇은 회색 하단 테두리 */
        /* 위, 왼쪽, 오른쪽 패딩/마진을 제거하기 위해 좌우 패딩을 0으로 설정 */
        padding: 1rem 0; /* 상하 1rem 유지, 좌우 0 */
        box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06); /* 부드러운 그림자 */
    }

    /* 헤더 내부 컨텐츠 (중앙 정렬 및 최대 너비 설정) */
    .header-content {
        max-width: 1200px; /* 최대 너비 설정 */
        margin: 0 auto; /* 중앙 정렬 */
        /* 컨테이너에서 제거된 좌우 여백을 내부 콘텐츠에 다시 추가하여 내용이 가장자리에 붙지 않도록 함 */
        padding: 0 2rem; 
        display: flex;
        justify-content: space-between; /* 양 끝 정렬 */
        align-items: center; /* 세로 중앙 정렬: 이 설정이 로고, 검색 영역, 메뉴를 정렬합니다. */
        gap: 1.5rem; /* 요소들 사이의 간격 */
        flex-wrap: nowrap; /* 줄바꿈 방지 */
    }

    /* 사이트 로고 */
    .site-logo {
        font-size: 1.75rem; 
        font-weight: bold; 
        color: #1E9447; 
        text-decoration: none; 
        white-space: nowrap; 
        flex-shrink: 0; 
    }

    .site-logo a {
        color: #1E9447; 
        text-decoration: none; 
    }

    .site-logo a:hover {
        color: #177a39; 
    }

    /* 검색 영역 */
    .search-area {
        flex-grow: 1; 
        max-width: 500px; 
        min-width: 200px; 
        align-self: center; 
    }

    .search-area form {
        display: flex;
        /* [핵심 수정]: 내부 인풋/버튼이 항상 중앙 정렬되도록 다시 확인 */
        align-items: center; 
        height: 100%; /* 부모 높이 상속 */
        border: 1px solid #d1d5db; 
        border-radius: 0.5rem; 
        overflow: hidden; 
        box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05); 
    }

    .search-input {
        flex-grow: 1; 
        /* 패딩을 유지하여 높이를 결정 */
        padding: 0.75rem 1rem; 
        border: none; 
        outline: none; 
        font-size: 0.95rem;
        color: #374151;
        /* [수정]: vertical-align 제거 - Flex 컨테이너에서는 불필요/방해될 수 있음 */
        /* vertical-align: middle; */
    }

    .search-input::placeholder {
        color: #9ca3af; 
    }

    .search-button {
        background-color: #f3f4f6; 
        border: none; 
        /* 인풋과 동일한 수직 패딩을 유지하여 높이를 맞춥니다. */
        padding: 0.75rem 1rem; 
        cursor: pointer;
        color: #6b7280; 
        font-size: 1.1rem;
        transition: background-color 0.15s ease-in-out; 
        /* [수정]: vertical-align 제거 - Flex 컨테이너에서는 불필요/방해될 수 있음 */
        /* vertical-align: middle; */
    }

    /* [추가]: 아이콘 폰트의 미세한 정렬 불균형 해결 */
    .search-button i {
        /* line-height를 0 또는 normal로 설정하여 폰트의 기본 여백을 무시하고 부모에 종속되도록 함 */
        line-height: normal;
        display: block; /* 블록 요소로 만들어 정렬 제어 용이 */
    }

    .search-button:hover {
        background-color: #e5e7eb; 
    }

    /* 사용자 메뉴 */
    .user-menu {
        display: flex;
        align-items: center;
        gap: 1rem; 
        white-space: nowrap; 
        flex-shrink: 0; 
    }

    .menu-item {
        color: #4b5563; 
        text-decoration: none; 
        font-weight: 500; 
        padding: 0.25rem 0.5rem; 
        transition: color 0.15s ease-in-out;
    }

    .menu-item:hover {
        color: #1E9447; 
    }

    .menu-divider {
        color: #d1d5db; 
        font-size: 1rem;
    }

    /* 모바일 반응형: 768px 이하에서는 다시 세로로 정렬되도록 변경하여 레이아웃 깨짐을 방지 */
    @media (max-width: 768px) {
        .header-content {
            flex-direction: column; 
            align-items: flex-start; 
            flex-wrap: wrap; 
        }
        .search-area {
            width: 100%; 
            max-width: none;
            min-width: 0; 
        }
        .user-menu {
            width: 100%;
            justify-content: flex-start; 
            margin-top: 0.5rem;
        }
    }
</style>
</head>


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
                <%-- 외부 CSS에 의존하는 Font Awesome 아이콘으로 복구 --%>
                <button type="submit" class="search-button">
                    <i class="fa fa-search"></i>
                </button>
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
