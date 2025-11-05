CREATE Table usermarketdb;
CREATE Table region;
USE usermarketdb;

-- 2) 회원
CREAT

USE usermarregion)E TABLE user (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  account_id  VARCHAR(30)  NOT NULL,
  pw          VARCHAR(255) NOT NULL,
  name        VARCHAR(50)  NOT NULL,
  phn         VARCHAR(20)  NULL,
  em          VARCHAR(100) NULL,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_user_account (account_id),
  UNIQUE KEY uk_user_email   (em),
  UNIQUE KEY uk_user_phone   (phn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3) 회원 프로필
CREATE TABLE user_info (
  u_id         INT NOT NULL PRIMARY KEY,
  nickname     VARCHAR(30) NOT NULL,
  profile_img  VARCHAR(255) NULL,
  intro        VARCHAR(255) NULL,
  region_id    INT NULL,
  addr_detail  VARCHAR(100) NULL,

  UNIQUE KEY uk_userinfo_uid (u_id),
  UNIQUE KEY uk_userinfo_nick (nickname),

  CONSTRAINT fk_userinfo_user
    FOREIGN KEY (u_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE RESTRICT,

  CONSTRAINT fk_userinfo_region
    FOREIGN KEY (region_id) REFERENCES region(id)
    ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


- ==========================================
-- 🚀 단감나라 / 유저마켓 통합 DB 초기화 스크립트
-- Database: usermarketdb
-- ==========================================

-- 0️⃣ 데이터베이스 생성 및 선택
CREATE DATABASE IF NOT EXISTS usermarketdb
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE usermarketdb;

-- 1️⃣ 회원 테이블
CREATE TABLE user (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  account_id  VARCHAR(30)  NOT NULL,
  pw          VARCHAR(255) NOT NULL,
  name        VARCHAR(50)  NOT NULL,
  phn         VARCHAR(20)  NULL,
  em          VARCHAR(100) NULL,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_user_account (account_id),
  UNIQUE KEY uk_user_email   (em),
  UNIQUE KEY uk_user_phone   (phn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2️⃣ 회원 프로필
CREATE TABLE user_info (
  u_id         INT NOT NULL PRIMARY KEY,
  nickname     VARCHAR(30) NOT NULL,
  profile_img  VARCHAR(255) NULL,
  intro        VARCHAR(255) NULL,
  addr_detail  VARCHAR(100) NULL,

  UNIQUE KEY uk_userinfo_uid (u_id),
  UNIQUE KEY uk_userinfo_nick (nickname),

  CONSTRAINT fk_userinfo_user
    FOREIGN KEY (u_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3️⃣ 카테고리
CREATE TABLE categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(20) NOT NULL
);

-- 4️⃣ 시도 지역
CREATE TABLE sido_areas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  adm_code VARCHAR(2) NOT NULL,
  name VARCHAR(50) NOT NULL,
  version VARCHAR(20) NOT NULL
);

-- 5️⃣ 시군구 지역
CREATE TABLE sigg_areas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  sido_area_id INT NOT NULL,
  adm_code VARCHAR(5) NOT NULL,
  name VARCHAR(50) NOT NULL,
  version TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (sido_area_id) REFERENCES sido_areas(id)
);

-- 6️⃣ 사용자 (거래용)
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  mobile_number VARCHAR(11) NOT NULL UNIQUE,
  activated BOOLEAN NOT NULL DEFAULT TRUE,
  rating_score DECIMAL(3,1) NOT NULL DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7️⃣ 활동 지역
CREATE TABLE activity_areas (
  user_id BIGINT NOT NULL,
  id2 INT NOT NULL,
  distance_meters SMALLINT NOT NULL,
  emd_area_ids JSON NOT NULL,
  authenticated_at TIMESTAMP NULL,
  PRIMARY KEY (user_id, id2),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (id2) REFERENCES sigg_areas(id)
);

-- 8️⃣ 파일
CREATE TABLE imgs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  uploader_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (uploader_id) REFERENCES users(id)
);

-- 9️⃣ 상품
CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  seller_id BIGINT NOT NULL,
  category_id INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  status ENUM('SALE', 'RESERVED', 'SOLD_OUT') NOT NULL DEFAULT 'SALE',
  sell_price INT NULL,
  view_count INT NOT NULL DEFAULT 0,
  description TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (seller_id) REFERENCES users(id),
  FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 🔟 상품 이미지
CREATE TABLE products_images (
  products_id BIGINT NOT NULL,
  img_id BIGINT NOT NULL,
  PRIMARY KEY (products_id, img_id),
  FOREIGN KEY (products_id) REFERENCES products(id) ON DELETE CASCADE,
  FOREIGN KEY (img_id) REFERENCES imgs(id)
);

-- 11️⃣ 팔로우
CREATE TABLE follow_users (
  user_id BIGINT NOT NULL,
  target_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, target_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (target_id) REFERENCES users(id)
);

-- 12️⃣ 찜 목록
CREATE TABLE wish_lists (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  register_id BIGINT NOT NULL,
  products_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (register_id) REFERENCES users(id),
  FOREIGN KEY (products_id) REFERENCES products(id)
);

-- ==========================
-- ✅ 더미데이터 (네가 보낸 내용 그대로)
-- ==========================

-- 카테고리
INSERT INTO categories (name)
VALUES 
('전자제품'),
('생활용품'),
('도서'),
('가전제품'),
('의류'),
('스포츠용품'),
('악세서리');

-- 사용자
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

-- 이미지
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

-- 상품
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

-- 상품 이미지 연결
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

-- 찜 목록
INSERT INTO wish_lists (register_id, products_id)
VALUES
(1, 4), (2, 1), (3, 2), (4, 3),
(5, 5), (6, 7), (7, 8), (8, 9), (9, 10), (10, 6);

-- 시도 지역
INSERT INTO sido_areas (adm_code, name, version)
VALUES
('11', '서울특별시', 'v1.0'),
('26', '부산광역시', 'v1.0'),
('27', '대구광역시', 'v1.0'),
('28', '인천광역시', 'v1.0'),
('41', '경기도', 'v1.0'),
('48', '경상남도', 'v1.0');

-- 시군구 지역
INSERT INTO sigg_areas (sido_area_id, adm_code, name, version)
VALUES
(1, '11110', '종로구', NOW()),
(1, '11680', '강남구', NOW()),
(2, '26350', '해운대구', NOW()),
(2, '26500', '수영구', NOW()),
(5, '41131', '수원시', NOW());

-- 활동 지역
INSERT INTO activity_areas (user_id, id2, distance_meters, emd_area_ids, authenticated_at)
VALUES
(1, 1, 1000, JSON_ARRAY(11680101, 11680102), NOW()),
(2, 2, 2000, JSON_ARRAY(11500101, 11500102), NOW()),
(3, 3, 1500, JSON_ARRAY(26350101, 26350102), NOW()),
(4, 4, 2500, JSON_ARRAY(26290101, 26290102), NOW()),
(5, 5, 1200, JSON_ARRAY(28245101, 28245102), NOW());

-- ==========================
-- ✅ 확인용 쿼리
-- ==========================
SELECT COUNT(*) AS products_count FROM products;
SELECT COUNT(*) AS users_count FROM users;
SELECT * FROM products LIMIT 5;

select * from products;
select * from sigg_areas;

SELECT p.id, p.title, sa.name, i.name
FROM products p
JOIN products_images pi ON p.id = pi.products_id
JOIN imgs i ON pi.img_id = i.id
JOIN users u ON p.seller_id = u.id
LEFT JOIN activity_areas aa ON u.id = aa.user_id
LEFT JOIN sigg_areas sa ON aa.id2 = sa.id;
