USE market_db;

-- ✅ 카테고리
INSERT INTO categories (name)
VALUES 
('전자제품'),
('생활용품'),
('도서'),
('가전제품'),
('의류'),
('스포츠용품'),
('악세서리');

-- ✅ 사용자 (10명)
INSERT INTO users (mobile_number, activated, rating_score)
VALUES 
('01011112222', TRUE, 4.8),
('01022223333', TRUE, 4.3),
('01033334444', TRUE, 3.9),
('01044445555', TRUE, 4.7),
('01055556666', TRUE, 3.5),
('01066667777', TRUE, 4.9),
('01077778888', TRUE, 4.2),
('01088889999', TRUE, 4.1),
('01099990000', TRUE, 3.8),
('01012345678', TRUE, 4.5);

-- ✅ 이미지 (20장)
INSERT INTO imgs (uploader_id, name)
VALUES 
(1, '../resources/images/test1.jpg'),
(1, '../resources/images/test2.jpg'),
(2, '../resources/images/test3.jpg'),
(2, '../resources/images/test4.jpg'),
(3, '../resources/images/test5.jpg'),
(3, '../resources/images/test6.jpg'),
(4, '../resources/images/test7.jpg'),
(4, '../resources/images/test8.jpg'),
(5, '../resources/images/test9.jpg'),
(5, '../resources/images/test10.jpg'),
(6, '../resources/images/test11.jpg'),
(6, '../resources/images/test12.jpg'),
(7, '../resources/images/test13.jpg'),
(7, '../resources/images/test14.jpg'),
(8, '../resources/images/test15.jpg'),
(8, '../resources/images/test16.jpg'),
(9, '../resources/images/test17.jpg'),
(9, '../resources/images/test18.jpg'),
(10, '../resources/images/test19.jpg'),
(10, '../resources/images/test20.jpg');

-- ✅ 상품 (10개)
INSERT INTO products (seller_id, category_id, title, sell_price, description)
VALUES
(1, 1, 'HDMI 미러링 동글', 5000, '티비연결용 HDMI 미러링 동글 팝니다.'),
(2, 2, '디지털 알람시계', 7000, '불빛나는 LED 디지털 시계 팝니다.'),
(3, 3, 'IT 전문서적', 10000, '자바 웹 개발 입문 교재 팝니다.'),
(4, 1, '블루투스 이어폰', 15000, '노이즈 캔슬링 이어폰, 새상품급'),
(5, 4, '미니 냉장고', 35000, '1인용 미니 냉장고 팝니다.'),
(6, 5, '패딩 점퍼', 25000, '겨울용 오리털 패딩 저렴하게 팝니다.'),
(7, 6, '요가 매트', 10000, '거의 새제품 요가 매트 팝니다.'),
(8, 1, '무선 키보드 세트', 12000, '무선 키보드 + 마우스 세트'),
(9, 7, '패션 목걸이', 9000, '골드 도금 목걸이, 포장 포함'),
(10, 2, '휴대용 선풍기', 6000, 'USB 충전식 휴대용 선풍기 팝니다.');

-- ✅ 상품 이미지 연결 (1:N)
INSERT INTO products_images (products_id, img_id)
VALUES 
(1, 1), (1, 2),
(2, 3), (2, 4),
(3, 5), (3, 6),
(4, 7), (4, 8),
(5, 9), (5, 10),
(6, 11), (6, 12),
(7, 13), (7, 14),
(8, 15), (8, 16),
(9, 17), (9, 18),
(10, 19), (10, 20);

-- ✅ 찜 목록 (wish_lists)
INSERT INTO wish_lists (register_id, products_id)
VALUES
(1, 4), (2, 1), (3, 2), (4, 3),
(5, 5), (6, 7), (7, 8), (8, 9), (9, 10), (10, 6);

-- ✅ 시도 지역 (기존 유지)
INSERT INTO sido_areas (adm_code, name, version)
VALUES
('11', '서울특별시', 'v1.0'),
('26', '부산광역시', 'v1.0'),
('27', '대구광역시', 'v1.0'),
('28', '인천광역시', 'v1.0'),
('41', '경기도', 'v1.0'),
('48', '경상남도', 'v1.0');

-- ✅ 활동 지역 (activity_areas)
INSERT INTO activity_areas (user_id, id2, distance_meters, emd_area_ids, authenticated_at)
VALUES
(1, 1, 1000, JSON_ARRAY(11680101, 11680102), NOW()),
(2, 2, 2000, JSON_ARRAY(11500101, 11500102), NOW()),
(3, 3, 1500, JSON_ARRAY(26350101, 26350102), NOW()),
(4, 4, 2500, JSON_ARRAY(26290101, 26290102), NOW()),
(5, 5, 1200, JSON_ARRAY(28245101, 28245102), NOW()),
(6, 6, 1800, JSON_ARRAY(41285101, 41285102), NOW()),
(7, 7, 3000, JSON_ARRAY(48310101, 48310102), NOW()),
(8, 1, 1000, JSON_ARRAY(11680103, 11680104), NOW()),
(9, 2, 2500, JSON_ARRAY(11500103, 11500104), NOW()),
(10, 3, 1000, JSON_ARRAY(26350103, 26350104), NOW());

INSERT INTO sigg_areas (sido_area_id, adm_code, name, version)
VALUES
-- 서울특별시 (sido_area_id = 1)
(1, '11110', '종로구', NOW()),
(1, '11140', '중구', NOW()),
(1, '11170', '용산구', NOW()),
(1, '11200', '성동구', NOW()),
(1, '11215', '광진구', NOW()),
(1, '11230', '동대문구', NOW()),
(1, '11260', '중랑구', NOW()),
(1, '11290', '성북구', NOW()),
(1, '11305', '강북구', NOW()),
(1, '11320', '도봉구', NOW()),
(1, '11350', '노원구', NOW()),
(1, '11380', '은평구', NOW()),
(1, '11410', '서대문구', NOW()),
(1, '11440', '마포구', NOW()),
(1, '11470', '양천구', NOW()),
(1, '11500', '강서구', NOW()),
(1, '11530', '구로구', NOW()),
(1, '11545', '금천구', NOW()),
(1, '11560', '영등포구', NOW()),
(1, '11590', '동작구', NOW()),
(1, '11620', '관악구', NOW()),
(1, '11650', '서초구', NOW()),
(1, '11680', '강남구', NOW()),
(1, '11710', '송파구', NOW()),
(1, '11740', '강동구', NOW()),

-- 부산광역시 (sido_area_id = 2)
(2, '26110', '중구', NOW()),
(2, '26140', '서구', NOW()),
(2, '26170', '동구', NOW()),
(2, '26200', '영도구', NOW()),
(2, '26230', '부산진구', NOW()),
(2, '26260', '동래구', NOW()),
(2, '26290', '남구', NOW()),
(2, '26320', '북구', NOW()),
(2, '26350', '해운대구', NOW()),
(2, '26380', '사하구', NOW()),
(2, '26410', '금정구', NOW()),
(2, '26440', '강서구', NOW()),
(2, '26470', '연제구', NOW()),
(2, '26500', '수영구', NOW()),
(2, '26530', '사상구', NOW()),
(2, '26710', '기장군', NOW());



SELECT * FROM users;
SELECT * FROM products_images;
SELECT * FROM imgs;
SELECT * FROM sigg_areas;
SELECT * FROM products;
SELECT * FROM activity_areas;

USE market_db;

SELECT
	p.id AS product_id,
	p.title AS product_name, 
    p.status, 
    p.sell_price, 
    sa.name AS sigg_name, 
    MIN(i.name) AS img_name
FROM 
	products p
JOIN products_images pi ON p.id = pi.products_id
JOIN imgs i ON pi.img_id = i.id
JOIN users u ON p.seller_id = u.id
JOIN activity_areas aa ON u.id = aa.user_id
JOIN sigg_areas sa ON aa.id2 = sa.id
JOIN sido_areas s ON sa.sido_area_id = s.id
GROUP BY p.id, p.title, p.status, p.sell_price, sa.name
ORDER BY p.created_at DESC;
	
