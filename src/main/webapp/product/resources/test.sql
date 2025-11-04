-- ==========================================
-- 🚀 단감나라 / 유저마켓 통합 DB 초기화 스크립트
-- Database: usermarketdb
-- ==========================================
DROP DATABASE usermarketdb;

CREATE DATABASE IF NOT EXISTS usermarketdb
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE usermarketdb;

-- ==========================================
-- 👤 회원 테이블
-- ==========================================
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


-- ==========================================
-- 📦 카테고리
-- ==========================================
CREATE TABLE categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(20) NOT NULL
);


-- ==========================================
-- 🗺️ 행정구역 (시도/시군구)
-- ==========================================
CREATE TABLE sido_areas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);

CREATE TABLE sigg_areas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  sido_area_id INT NOT NULL,
  name VARCHAR(50) NOT NULL,
  FOREIGN KEY (sido_area_id) REFERENCES sido_areas(id)
    ON DELETE CASCADE ON UPDATE CASCADE
);


-- ==========================================
-- 📍 활동 지역
-- ==========================================
CREATE TABLE activity_areas (
  user_id INT NOT NULL,
  sigg_area_id INT NOT NULL,
  distance_meters SMALLINT NOT NULL DEFAULT 2000,
  emd_area_ids JSON NOT NULL,
  authenticated_at TIMESTAMP NULL,
  PRIMARY KEY (user_id, sigg_area_id),
  FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (sigg_area_id) REFERENCES sigg_areas(id)
    ON DELETE CASCADE ON UPDATE CASCADE
);


-- ==========================================
-- 🖼️ 이미지 파일
-- ==========================================
CREATE TABLE imgs (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uploader_id INT NOT NULL,
  name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (uploader_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE CASCADE
);


-- ==========================================
-- 🛒 상품
-- ==========================================
CREATE TABLE products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  seller_id INT NOT NULL,
  category_id INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  status ENUM('SALE', 'RESERVED', 'SOLD_OUT') NOT NULL DEFAULT 'SALE',
  sell_price INT NULL,
  view_count INT NOT NULL DEFAULT 0,
  description TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (seller_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (category_id) REFERENCES categories(id)
    ON DELETE CASCADE ON UPDATE CASCADE
);


-- ==========================================
-- 🖼️ 상품 이미지
-- ==========================================
CREATE TABLE products_images (
  products_id INT NOT NULL,
  img_id INT NOT NULL,
  PRIMARY KEY (products_id, img_id),
  FOREIGN KEY (products_id) REFERENCES products(id) ON DELETE CASCADE,
  FOREIGN KEY (img_id) REFERENCES imgs(id) ON DELETE CASCADE
);


-- ==========================================
-- 💖 찜 목록
-- ==========================================
CREATE TABLE wish_lists (
  id INT AUTO_INCREMENT PRIMARY KEY,
  register_id INT NOT NULL,
  products_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (register_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (products_id) REFERENCES products(id)
    ON DELETE CASCADE ON UPDATE CASCADE
);


-- ==========================================
-- 전국 행정구역 데이터 초기화
-- 대상 테이블: sido_areas / sigg_areas
-- ==========================================

-- ======================
-- 시도 데이터 입력
-- ======================
INSERT INTO sido_areas (id, name) VALUES
(1, '서울특별시'),
(2, '부산광역시'),
(3, '대구광역시'),
(4, '인천광역시'),
(5, '광주광역시'),
(6, '대전광역시'),
(7, '울산광역시'),
(8, '세종특별자치시'),
(9, '경기도'),
(10, '강원특별자치도'),
(11, '충청북도'),
(12, '충청남도'),
(13, '전북특별자치도'),
(14, '전라남도'),
(15, '경상북도'),
(16, '경상남도'),
(17, '제주특별자치도');

-- ======================
-- 시군구 데이터 입력
-- ======================

-- 서울특별시
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(1, '종로구'), (1, '중구'), (1, '용산구'), (1, '성동구'), (1, '광진구'),
(1, '동대문구'), (1, '중랑구'), (1, '성북구'), (1, '강북구'), (1, '도봉구'),
(1, '노원구'), (1, '은평구'), (1, '서대문구'), (1, '마포구'), (1, '양천구'),
(1, '강서구'), (1, '구로구'), (1, '금천구'), (1, '영등포구'), (1, '동작구'),
(1, '관악구'), (1, '서초구'), (1, '강남구'), (1, '송파구'), (1, '강동구');

-- 부산광역시
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(2, '중구'), (2, '서구'), (2, '동구'), (2, '영도구'), (2, '부산진구'),
(2, '동래구'), (2, '남구'), (2, '북구'), (2, '해운대구'), (2, '사하구'),
(2, '금정구'), (2, '강서구'), (2, '연제구'), (2, '수영구'), (2, '사상구'),
(2, '기장군');

-- 대구광역시
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(3, '중구'), (3, '동구'), (3, '서구'), (3, '남구'), (3, '북구'),
(3, '수성구'), (3, '달서구'), (3, '달성군'), (3, '군위군');

-- 인천광역시
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(4, '중구'), (4, '동구'), (4, '미추홀구'), (4, '연수구'), (4, '남동구'),
(4, '부평구'), (4, '계양구'), (4, '서구'), (4, '강화군'), (4, '옹진군');

-- 광주광역시
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(5, '동구'), (5, '서구'), (5, '남구'), (5, '북구'), (5, '광산구');

-- 대전광역시
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(6, '동구'), (6, '중구'), (6, '서구'), (6, '유성구'), (6, '대덕구');

-- 울산광역시
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(7, '중구'), (7, '남구'), (7, '동구'), (7, '북구'), (7, '울주군');

-- 세종특별자치시
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(8, '세종시');

-- 경기도
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(9, '수원시'), (9, '성남시'), (9, '의정부시'), (9, '안양시'), (9, '부천시'),
(9, '광명시'), (9, '평택시'), (9, '동두천시'), (9, '안산시'), (9, '고양시'),
(9, '과천시'), (9, '구리시'), (9, '남양주시'), (9, '오산시'), (9, '시흥시'),
(9, '군포시'), (9, '의왕시'), (9, '하남시'), (9, '용인시'), (9, '파주시'),
(9, '이천시'), (9, '안성시'), (9, '김포시'), (9, '화성시'), (9, '광주시'),
(9, '양주시'), (9, '포천시'), (9, '여주시'), (9, '연천군'), (9, '가평군'),
(9, '양평군');

-- 강원특별자치도
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(10, '춘천시'), (10, '원주시'), (10, '강릉시'), (10, '동해시'), (10, '태백시'),
(10, '속초시'), (10, '삼척시'), (10, '홍천군'), (10, '횡성군'), (10, '영월군'),
(10, '평창군'), (10, '정선군'), (10, '철원군'), (10, '화천군'), (10, '양구군'),
(10, '인제군'), (10, '고성군'), (10, '양양군');

-- 충청북도
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(11, '청주시'), (11, '충주시'), (11, '제천시'), (11, '보은군'), (11, '옥천군'),
(11, '영동군'), (11, '진천군'), (11, '괴산군'), (11, '음성군'), (11, '단양군'),
(11, '증평군');

-- 충청남도
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(12, '천안시'), (12, '공주시'), (12, '보령시'), (12, '아산시'), (12, '서산시'),
(12, '논산시'), (12, '계룡시'), (12, '당진시'), (12, '금산군'), (12, '부여군'),
(12, '서천군'), (12, '청양군'), (12, '홍성군'), (12, '예산군'), (12, '태안군');

-- 전북특별자치도
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(13, '전주시'), (13, '군산시'), (13, '익산시'), (13, '정읍시'), (13, '남원시'),
(13, '김제시'), (13, '완주군'), (13, '진안군'), (13, '무주군'), (13, '장수군'),
(13, '임실군'), (13, '순창군'), (13, '고창군'), (13, '부안군');

-- 전라남도
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(14, '목포시'), (14, '여수시'), (14, '순천시'), (14, '나주시'), (14, '광양시'),
(14, '담양군'), (14, '곡성군'), (14, '구례군'), (14, '고흥군'), (14, '보성군'),
(14, '화순군'), (14, '장흥군'), (14, '강진군'), (14, '해남군'), (14, '영암군'),
(14, '무안군'), (14, '함평군'), (14, '영광군'), (14, '장성군'), (14, '완도군'),
(14, '진도군'), (14, '신안군');

-- 경상북도
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(15, '포항시'), (15, '경주시'), (15, '김천시'), (15, '안동시'), (15, '구미시'),
(15, '영주시'), (15, '영천시'), (15, '상주시'), (15, '문경시'), (15, '경산시'),
(15, '군위군'), (15, '의성군'), (15, '청송군'), (15, '영양군'), (15, '영덕군'),
(15, '청도군'), (15, '고령군'), (15, '성주군'), (15, '칠곡군'), (15, '예천군'),
(15, '봉화군'), (15, '울진군'), (15, '울릉군');

-- 경상남도
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(16, '창원시'), (16, '진주시'), (16, '통영시'), (16, '사천시'), (16, '김해시'),
(16, '밀양시'), (16, '거제시'), (16, '양산시'), (16, '의령군'), (16, '함안군'),
(16, '창녕군'), (16, '고성군'), (16, '남해군'), (16, '하동군'), (16, '산청군'),
(16, '함양군'), (16, '거창군'), (16, '합천군');

-- 제주특별자치도
INSERT INTO sigg_areas (sido_area_id, name) VALUES
(17, '제주시'), (17, '서귀포시');

-- =================================
-- 카테고리
INSERT INTO categories (name)
VALUES
('전자제품'),
('가구'),
('의류'),
('도서'),
('생활용품'),
('스포츠'),
('취미/게임'),
('기타');


SELECT * from user;
select * from products;
select * from products_images;
select * from categories;
select * from sido_areas;
select * from sigg_areas;
select * from activity_areas;
select * from imgs;
select * from user_info;