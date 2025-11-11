// mypage.js

// ---------------- 마이페이지 탭 로직 ----------------

/**
 * 탭을 변경하고 해당 탭의 콘텐츠를 표시합니다.
 * @param {string} tabName - 활성화할 탭의 이름 (예: 'products', 'wishlist')
 */
function changeTab(tabName) {
	// 1. 모든 탭 버튼 스타일 초기화
	document.querySelectorAll('.tab-button').forEach(button => {
		button.classList.remove('text-green-500', 'border-green-500');
		button.classList.add('text-gray-500', 'border-transparent', 'hover:border-gray-300');
	});

	// 2. 모든 탭 콘텐츠 숨김
	document.querySelectorAll('.tab-content').forEach(content => {
		content.classList.add('hidden');
	});

	// 3. 선택된 탭 활성화
	const selectedButton = document.getElementById(`tab-${tabName}`);
	if (selectedButton) {
		selectedButton.classList.add('text-green-500', 'border-green-500');
		selectedButton.classList.remove('text-gray-500', 'border-transparent', 'hover:border-gray-300');
	}

	// 4. 선택된 콘텐츠 표시
	const selectedContent = document.getElementById(`content-${tabName}`);
	if (selectedContent) {
		selectedContent.classList.remove('hidden');
	}

    // 5. 찜 목록 탭 선택 시 데이터 로드
    if (tabName === 'wishlist') {
        loadWishlist();
    }
    // TODO: 'products' 탭 선택 시 등록 상품 목록을 불러오는 loadProducts() 함수도 필요할 수 있습니다.
}

// ---------------- 찜 목록 로직 (AJAX) ----------------

/**
 * 찜 목록 데이터를 AJAX로 불러와 화면에 렌더링하는 함수
 */
function loadWishlist() {
    // myPage.jsp 파일에서 window.contextPath가 설정되어 있어야 합니다.
    const contextPath = window.contextPath || '';
    const wishlistContainer = document.getElementById('content-wishlist');
    const apiUrl = contextPath + '/user/mypage/wishlist/list';

    // 로딩 표시
    wishlistContainer.innerHTML = `
        <div class="p-4 bg-gray-50 rounded-lg border border-gray-100 text-center">
            <p class="text-gray-600">찜 목록을 불러오는 중...</p>
        </div>
    `;

    fetch(apiUrl)
        .then(response => {
            if (response.status === 401) {
                // 로그인 필요 (서블릿에서 401 반환 시 처리)
                alert("세션이 만료되었거나 로그인이 필요합니다.");
                window.location.href = contextPath + "/user/login";
                return Promise.reject("로그인 필요");
            }
            if (!response.ok) {
                // 서버 오류 또는 400 Bad Request 등의 처리
                return response.json().then(err => {
                    throw new Error(err.error || '목록 로드에 실패했습니다.');
                });
            }
            return response.json();
        })
        .then(products => {
            renderWishlist(products, wishlistContainer, contextPath);
        })
        .catch(error => {
            console.error("Error loading wishlist:", error);
            if (error.message !== "로그인 필요") {
                wishlistContainer.innerHTML = `
                    <div class="p-4 bg-red-50 rounded-lg border border-red-200">
                        <p class="text-red-600">오류 발생: ${error.message}</p>
                    </div>
                `;
            }
        });
}

/**
 * 받은 상품 목록 데이터를 HTML로 변환하여 컨테이너에 삽입합니다.
 * @param {Array<Object>} products - 찜 목록 상품 데이터 배열
 * @param {HTMLElement} container - 찜 목록을 렌더링할 컨테이너 요소
 * @param {string} contextPath - 애플리케이션의 컨텍스트 경로
 */
function renderWishlist(products, container, contextPath) {
    if (products.length === 0) {
        // 찜 목록이 없을 때의 기본 메시지
        container.innerHTML = `
            <div class="p-4 bg-gray-50 rounded-lg border border-gray-100">
                <p class="text-gray-600">찜한 상품이 없습니다.</p>
                <a href="${contextPath}/product/list"
                    class="text-green-500 font-semibold hover:underline mt-2 inline-block">상품 구경가기 &rarr;</a>
            </div>
        `;
        return;
    }

    // 상품 목록 그리드 HTML 생성 (카드 형식)
    let productHtml = '<div class="grid grid-cols-1 sm:grid-cols-2 gap-4">';

    products.forEach(product => {
        // 가격 포맷팅 (예: 10000 -> 10,000)
        const formattedPrice = product.sellPrice.toLocaleString();

        productHtml += `
            <a href="${contextPath}/product/detail?id=${product.id}" class="block">
                <div class="bg-white rounded-lg shadow-md hover:shadow-lg transition duration-150 overflow-hidden flex">
                    <img src="${contextPath}${product.displayImg}"
                         alt="${product.title}"
                         class="w-24 h-24 object-cover flex-shrink-0 rounded-l-lg">

                    <div class="p-3 flex-grow min-w-0">
                        <h3 class="text-md font-bold text-gray-800 truncate">${product.title}</h3>
                        <p class="text-sm text-red-500 font-semibold mt-1">${formattedPrice}원</p>
                        <p class="text-xs text-gray-500 mt-1">${product.siggName || '지역 정보 없음'}</p>
                    </div>
                </div>
            </a>
        `;
    });

    productHtml += '</div>';
    container.innerHTML = productHtml;
}


// ---------------- 전역 및 초기화 로직 ----------------

// 전역 이벤트 리스너: 페이지 로드 후 실행
document.addEventListener('DOMContentLoaded', () => {
    // Lucide 아이콘 초기화 (mypage.jsp에 <script src="https://unpkg.com/lucide@latest"></script>가 있어야 작동)
    if (typeof initializeIcons === 'function') {
        initializeIcons();
    } else if (window.lucide) {
        lucide.createIcons();
    }

    // 탭 버튼 클릭 이벤트 리스너 설정
    // myPage.jsp의 탭 버튼에 직접 `onclick="changeTab('탭이름')"`을 추가하는 것이 더 효율적일 수 있습니다.
    // 여기서는 myPage.jsp에 이벤트 리스너를 명시적으로 추가해야 한다는 주석에 따라, DOM이 로드된 후에도 기본 탭을 'products'로 설정합니다.
    if (document.getElementById('tab-products')) {
        changeTab('products');
    }

    // TODO: myPage.jsp에 탭 버튼에 이벤트 리스너를 명시적으로 추가하여 changeTab을 호출하도록 수정해야 합니다.
    // 예: document.getElementById('tab-wishlist').addEventListener('click', () => changeTab('wishlist'));
    // 예: document.getElementById('tab-products').addEventListener('click', () => changeTab('products'));

    // 회원가입 폼의 경우 onSubmit 이벤트 재연결 (기존 로직 유지)
    const addForm = document.querySelector('form[name="newUser"]');
    if (addForm) {
        addForm.onsubmit = checkAddForm;
    }

    // 회원수정 폼의 경우 onSubmit 이벤트 재연결 (기존 로직 유지)
    const updateForm = document.querySelector('form[name="updateUser"]');
    if (updateForm) {
        updateForm.onsubmit = checkUpdateForm;
    }
});