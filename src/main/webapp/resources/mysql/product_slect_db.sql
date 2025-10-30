
SELECT * FROM users;
SELECT * FROM products_images;
SELECT * FROM imgs;
SELECT * FROM sigg_areas;
SELECT * FROM products;
SELECT * FROM activity_areas;
SELECT id, name FROM sido_areas;


-- ui 도를 통행 읍면동 가져오기
SELECT 
    sa.id AS sigg_id,
    sa.name AS sigg_name
FROM sigg_areas sa
JOIN sido_areas s ON sa.sido_area_id = s.id
WHERE s.id = (
    SELECT s_inner.id
    FROM users u
    JOIN activity_areas aa ON u.id = aa.user_id
    JOIN sigg_areas sa_inner ON aa.id2 = sa_inner.id
    JOIN sido_areas s_inner ON sa_inner.sido_area_id = s_inner.id
    WHERE u.id = 5
);


-- 사용자 id 통해 도 찾기
SELECT 
    s.id AS sido_id,
    s.name AS sido_name
FROM users u
JOIN activity_areas aa ON u.id = aa.user_id
JOIN sigg_areas sa ON aa.id2 = sa.id
JOIN sido_areas s ON sa.sido_area_id = s.id
WHERE u.id = 8;

-- 목록을 위한 정보 검색
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
	
    -- 카테고리 찾기
SELECT id, name AS categoire FROM categories;