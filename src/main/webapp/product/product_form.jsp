<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="card p-3">
    <form id="productForm" method="post" enctype="multipart/form-data" action="${formAction}">
        <div class="mb-3">
            <label for="title" class="form-label">상품명</label>
            <input type="text" class="form-control" id="title" name="title" required value="${product.title}">
        </div>

        <div class="mb-3">
            <label for="description" class="form-label">설명</label>
            <textarea class="form-control" id="description" name="description" rows="5">${product.description}</textarea>
        </div>

        <div class="row mb-3">
            <div class="col-md-4">
                <label for="sellPrice" class="form-label">가격(원)</label>
                <input type="number" class="form-control" id="sellPrice" name="sellPrice" required value="${product.sellPrice}">
            </div>
            <div class="col-md-4">
                <label for="status" class="form-label">상태</label>
                <select class="form-select" id="status" name="status">
                    <option value="판매중" ${product.status == '판매중' ? 'selected' : ''}>판매중</option>
                    <option value="예약중" ${product.status == '예약중' ? 'selected' : ''}>예약중</option>
                    <option value="거래완료" ${product.status == '거래완료' ? 'selected' : ''}>거래완료</option>
                </select>
            </div>
            <div class="col-md-4">
                <label for="sigg" class="form-label">지역</label>
                <select class="form-select" id="sigg" name="siggId">
                    <option value="">선택</option>
                    <c:forEach var="sigg" items="${siggList}">
                        <option value="${sigg.id}" ${product.siggId == sigg.id ? 'selected' : ''}>${sigg.name}</option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label">카테고리</label>
            <div>
                <c:forEach var="cat" items="${categoryList}">
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="categoryId" id="cat_${cat.id}" value="${cat.id}" ${product.categoryId == cat.id ? 'checked' : ''}>
                        <label class="form-check-label" for="cat_${cat.id}">${cat.name}</label>
                    </div>
                </c:forEach>
            </div>
        </div>

        <div class="mb-3">
            <label for="images" class="form-label">이미지 업로드</label>
            <input class="form-control" type="file" id="images" name="images" accept="image/*" multiple>
            <c:if test="${not empty product.imgSrc}">
                <div class="mt-2">
                    <p class="mb-1">현재 이미지</p>
                    <img src="${product.imgSrc}" alt="product" style="max-width:200px;"/>
                </div>
            </c:if>
        </div>

        <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary">저장</button>
            <a href="${pageContext.request.contextPath}/product/list" class="btn btn-secondary">취소</a>
        </div>
    </form>
</div>
