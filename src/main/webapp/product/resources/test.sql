-- ==========================================
-- 🚀 단감나라 / 유저마켓 통합 DB 초기화 스크립트
-- Database: usermarketdb
-- ==========================================

DROP DATABASE IF EXISTS usermarketdb;

CREATE DATABASE IF NOT EXISTS usermarketdb
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE usermarketdb;

-- ==========================================
-- 🗺️ 행정구역 (시도 / 시군구)
-- ==========================================

CREATE TABLE sido_areas (
  id   INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sigg_areas (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  sido_area_id INT NOT NULL,
  name         VARCHAR(50) NOT NULL,
  FOREIGN KEY (sido_area_id) REFERENCES sido_areas(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 👤 회원 / 프로필
-- ==========================================

CREATE TABLE user (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  account_id VARCHAR(30)   NOT NULL,
  pw         VARCHAR(255)  NOT NULL,
  name       VARCHAR(50)   NOT NULL,
  phn        VARCHAR(20)   NULL,
  em         VARCHAR(100)  NULL,
  created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_user_account (account_id),
  UNIQUE KEY uk_user_email   (em),
  UNIQUE KEY uk_user_phone   (phn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_info (
  u_id        INT NOT NULL PRIMARY KEY,
  nickname    VARCHAR(30)  NOT NULL,
  profile_img VARCHAR(255) NULL,
  intro       VARCHAR(255) NULL,
  region_id   INT          NULL,
  addr_detail VARCHAR(100) NULL,

  UNIQUE KEY uk_userinfo_uid  (u_id),
  UNIQUE KEY uk_userinfo_nick (nickname),

  CONSTRAINT fk_userinfo_user
    FOREIGN KEY (u_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE RESTRICT,

  CONSTRAINT fk_userinfo_region
    FOREIGN KEY (region_id) REFERENCES sigg_areas(id)
    ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 📦 카테고리
-- ==========================================

CREATE TABLE categories (
  id   INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(20) NOT NULL,
  UNIQUE KEY uk_category_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 📍 활동 지역
-- ==========================================

CREATE TABLE activity_areas (
  user_id        INT       NOT NULL,
  sigg_area_id   INT       NOT NULL,
  distance_meters SMALLINT NOT NULL DEFAULT 2000,
  emd_area_ids   JSON      NOT NULL,
  authenticated_at TIMESTAMP NULL,
  PRIMARY KEY (user_id, sigg_area_id),
  FOREIGN KEY (user_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (sigg_area_id) REFERENCES sigg_areas(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CHECK (JSON_VALID(emd_area_ids))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 🖼️ 이미지 파일
-- ==========================================

CREATE TABLE images (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  uploader_id INT NOT NULL,
  name       VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (uploader_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 🛒 상품
-- ==========================================

CREATE TABLE products (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  seller_id   INT NOT NULL,
  category_id INT NOT NULL,
  title       VARCHAR(100) NOT NULL,
  status      ENUM('SALE', 'RESERVED', 'SOLD_OUT') NOT NULL DEFAULT 'SALE',
  sell_price  INT NULL,
  view_count  INT NOT NULL DEFAULT 0,
  description TEXT NOT NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  sido_id     INT NULL,
  region_id   INT NULL,

  FOREIGN KEY (seller_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (category_id) REFERENCES categories(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (sido_id) REFERENCES sido_areas(id)
    ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (region_id) REFERENCES sigg_areas(id)
    ON DELETE SET NULL ON UPDATE CASCADE,

  KEY idx_products_category (category_id),
  KEY idx_products_region   (region_id),
  KEY idx_products_sido     (sido_id),
  KEY idx_products_seller   (seller_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 🖼️ 상품 이미지 매핑
-- ==========================================

CREATE TABLE product_images (
  product_id INT NOT NULL,
  image_id   INT NOT NULL,
  PRIMARY KEY (product_id, image_id),
  FOREIGN KEY (product_id) REFERENCES products(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (image_id) REFERENCES images(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 💖 찜 목록
-- ==========================================

CREATE TABLE wish_lists (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  register_id INT NOT NULL,
  product_id  INT NOT NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  -- ✅ 같은 유저가 같은 상품 여러 번 찜 못하게
  UNIQUE KEY uq_wish_user_product (register_id, product_id),

  FOREIGN KEY (register_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 💬 채팅 관련 테이블
-- ==========================================

CREATE TABLE chat_room (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    buyer_id   INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id) REFERENCES products(id)
      ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (buyer_id) REFERENCES user(id)
      ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE chat_messages (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    chat_room_id INT NOT NULL,
    sender_id   INT NOT NULL,
    message     TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (chat_room_id) REFERENCES chat_room(id)
      ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES user(id)
      ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 💬 평점 관련 테이블
-- ==========================================
CREATE TABLE seller_ratings (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  seller_id   INT NOT NULL,   -- 평가 받는 사람 (판매자: user.id)
  buyer_id    INT NOT NULL,   -- 평가하는 사람 (구매자: user.id)
  product_id  INT NOT NULL,   -- 어떤 상품 거래에 대한 평점인지
  rating      TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5), -- 1~5점
  comment     VARCHAR(255) NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  -- 한 구매자가 하나의 상품에 대해 한 번만 평가하게
  UNIQUE KEY uq_seller_rating (buyer_id, product_id),

  FOREIGN KEY (seller_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (buyer_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


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



-- ==========================================
-- 👤 회원 (6명)
-- ==========================================
INSERT INTO user (account_id, pw, name, phn, em) VALUES
('user001', '1234', '홍길동', '010-1111-1111', 'hong@test.com'),
('user002', '1234', '김철수', '010-2222-2222', 'kim@test.com'),
('user003', '1234', '이영희', '010-3333-3333', 'lee@test.com'),
('user004', '1234', '박민수', '010-4444-4444', 'park@test.com'),
('user005', '1234', '최유진', '010-5555-5555', 'choi@test.com'),
('user006', '1234', '정은지', '010-6666-6666', 'jung@test.com');

-- ==========================================
-- 🧑‍💻 유저 정보
-- ==========================================
INSERT INTO user_info (u_id, nickname, profile_img, intro, region_id, addr_detail) VALUES
(1, '단감홍', 'user001.jpg', '전자제품 전문 판매자입니다.', 1, '서울특별시 종로구 단감로 10'),
(2, '철수형', 'user002.jpg', '가구 리폼 전문가예요.', 26, '부산광역시 해운대구 바다로 123'),
(3, '책순이', 'user003.jpg', '책과 도서를 좋아해요.', 41, '대구광역시 수성구 행복로 55'),
(4, '운동맨', 'user004.jpg', '운동기구 및 스포츠 용품 판매자', 52, '인천광역시 연수구 센트럴로 77'),
(5, '패션유진', 'user005.jpg', '의류, 패션 소품을 주로 판매합니다.', 57, '광주광역시 서구 패션로 88'),
(6, '살림여왕', 'user006.jpg', '생활용품 전문 셀러입니다.', 62, '대전광역시 유성구 홈로 12');

-- ==========================================
-- 📍 활동 지역
-- ==========================================
INSERT INTO activity_areas (user_id, sigg_area_id, distance_meters, emd_area_ids)
VALUES
(1, 1, 3000, JSON_ARRAY(101,102)),
(2, 26, 2000, JSON_ARRAY(201,202)),
(3, 41, 1500, JSON_ARRAY(301,302)),
(4, 52, 2500, JSON_ARRAY(401,402)),
(5, 57, 1800, JSON_ARRAY(501,502)),
(6, 62, 2200, JSON_ARRAY(601,602));

-- ==========================================
-- 🖼️ 이미지 파일 (60개)
-- ==========================================
INSERT INTO images (uploader_id, name) VALUES
(1, 'user001_1.jpg'), (1, 'user001_2.jpg'), (1, 'user001_3.jpg'), (1, 'user001_4.jpg'), (1, 'user001_5.jpg'),
(1, 'user001_6.jpg'), (1, 'user001_7.jpg'), (1, 'user001_8.jpg'), (1, 'user001_9.jpg'), (1, 'user001_10.jpg'),
(2, 'user002_1.jpg'), (2, 'user002_2.jpg'), (2, 'user002_3.jpg'), (2, 'user002_4.jpg'), (2, 'user002_5.jpg'),
(2, 'user002_6.jpg'), (2, 'user002_7.jpg'), (2, 'user002_8.jpg'), (2, 'user002_9.jpg'), (2, 'user002_10.jpg'),
(3, 'user003_1.jpg'), (3, 'user003_2.jpg'), (3, 'user003_3.jpg'), (3, 'user003_4.jpg'), (3, 'user003_5.jpg'),
(3, 'user003_6.jpg'), (3, 'user003_7.jpg'), (3, 'user003_8.jpg'), (3, 'user003_9.jpg'), (3, 'user003_10.jpg'),
(4, 'user004_1.jpg'), (4, 'user004_2.jpg'), (4, 'user004_3.jpg'), (4, 'user004_4.jpg'), (4, 'user004_5.jpg'),
(4, 'user004_6.jpg'), (4, 'user004_7.jpg'), (4, 'user004_8.jpg'), (4, 'user004_9.jpg'), (4, 'user004_10.jpg'),
(5, 'user005_1.jpg'), (5, 'user005_2.jpg'), (5, 'user005_3.jpg'), (5, 'user005_4.jpg'), (5, 'user005_5.jpg'),
(5, 'user005_6.jpg'), (5, 'user005_7.jpg'), (5, 'user005_8.jpg'), (5, 'user005_9.jpg'), (5, 'user005_10.jpg'),
(6, 'user006_1.jpg'), (6, 'user006_2.jpg'), (6, 'user006_3.jpg'), (6, 'user006_4.jpg'), (6, 'user006_5.jpg'),
(6, 'user006_6.jpg'), (6, 'user006_7.jpg'), (6, 'user006_8.jpg'), (6, 'user006_9.jpg'), (6, 'user006_10.jpg');

-- ==========================================
-- 🛒 상품 (회원별 10개씩 총 60개)
-- ==========================================
INSERT INTO products (seller_id, category_id, title, status, sell_price, description, sido_id, region_id) VALUES
-- 1️⃣ 홍길동 (전자 + 스포츠 + 생활용품)
(1, 1, '맥북 에어 M2 13인치', 'SALE', 1450000, 'M2 칩, 실버, 거의 새것', 1, 10),
(1, 6, '홈트 요가매트 세트', 'SALE', 25000, 'NBR 재질, 쿠션감 우수', 1, 10),
(1, 5, '무선 LED 스탠드', 'SALE', 32000, '충전식, 3단 밝기 조절', 1, 10),
(1, 1, '아이패드 프로 12.9 5세대', 'RESERVED', 1250000, '스페이스그레이, 박스 포함', 1, 10),
(1, 2, '북유럽 디자인 의자', 'SALE', 95000, '화이트톤, 인테리어용', 1, 10),
(1, 7, '닌텐도 스위치 OLED', 'SALE', 360000, '화이트, 사용감 거의 없음', 1, 10),
(1, 5, '자동 디스펜서 세트', 'SALE', 28000, '손소독제, 주방용 모두 가능', 1, 10),
(1, 6, '덤벨 10kg 2개 세트', 'SALE', 55000, '고무코팅, 그립감 우수', 1, 10),
(1, 1, '소니 블루투스 이어폰 WF-1000XM5', 'SALE', 280000, '노이즈 캔슬링 탑재', 1, 10),
(1, 5, '전기 커튼 리모컨 세트', 'SALE', 89000, '스마트홈 호환', 1, 10),

-- 2️⃣ 김철수 (가구 + 전자 + 생활용품)
(2, 2, '2인용 원목 식탁', 'SALE', 120000, '튼튼한 오크 원목', 2, 12),
(2, 1, '32인치 스마트TV', 'SALE', 180000, '넷플릭스 지원, A급', 2, 12),
(2, 5, '공기청정기 미개봉', 'SALE', 135000, '10평형, 필터 포함', 2, 12),
(2, 2, '수납형 침대 프레임', 'RESERVED', 280000, '퀸사이즈, 상태 양호', 2, 12),
(2, 5, '가습기 & 아로마 세트', 'SALE', 40000, '겨울철 필수템', 2, 12),
(2, 1, '삼성 노트북 15인치', 'SALE', 490000, 'i5, 8GB RAM', 2, 12),
(2, 2, '라탄 수납 바구니 세트', 'SALE', 25000, '3개 구성, 내츄럴 스타일', 2, 12),
(2, 5, '무선 청소기', 'SOLD_OUT', 155000, '가벼운 무게, 고흡입력', 2, 12),
(2, 1, 'LG 모니터 27인치', 'SALE', 210000, 'IPS 패널, sRGB 지원', 2, 12),
(2, 2, '거실 테이블 세트', 'SALE', 140000, '화이트 & 원목 조합', 2, 12),

-- 3️⃣ 이영희 (도서 + 의류 + 전자)
(3, 4, '인문학 도서 20권 세트', 'SALE', 45000, '깨끗한 상태', 3, 14),
(3, 3, '여성용 자켓', 'SALE', 65000, '가을용, 새상품', 3, 14),
(3, 4, '경제경영 베스트셀러 10권', 'SALE', 38000, '투자서 포함', 3, 14),
(3, 1, '전자책 리더기', 'SALE', 120000, '라이트 지원, 상태 좋음', 3, 14),
(3, 3, '니트 가디건', 'SALE', 42000, '부드러운 촉감', 3, 14),
(3, 4, '소설세트 (한국문학)', 'SALE', 30000, '10권 묶음', 3, 14),
(3, 3, '데님 셔츠', 'SALE', 35000, '빈티지 감성', 3, 14),
(3, 1, '아이폰 14 미개봉', 'SOLD_OUT', 1500000, '256GB, 블랙', 3, 14),
(3, 4, '심리학 입문서', 'SALE', 17000, '표지 약간 손상', 3, 14),
(3, 3, '롱스커트', 'SALE', 48000, '플라워 패턴', 3, 14),

-- 4️⃣ 박민수 (스포츠 + 생활용품 + 전자)
(4, 6, '아령세트 20kg', 'SALE', 59000, '철제, 홈트용', 4, 16),
(4, 5, '전기주전자', 'SALE', 33000, '1.5L, 새상품', 4, 16),
(4, 1, '갤럭시 버즈2 프로', 'SALE', 190000, '화이트, 미개봉', 4, 16),
(4, 6, '실내자전거', 'RESERVED', 145000, '소음 적음', 4, 16),
(4, 5, '무선 다리미', 'SALE', 55000, '스팀형, 미사용', 4, 16),
(4, 1, '삼성 태블릿 A9', 'SALE', 270000, '10.4인치, A급', 4, 16),
(4, 6, '푸쉬업바 세트', 'SALE', 15000, '가정용', 4, 16),
(4, 5, '진공 청소기', 'SALE', 90000, '강력 흡입력', 4, 16),
(4, 2, '캠핑 테이블', 'SALE', 75000, '폴딩형, 알루미늄', 4, 16),
(4, 7, '플레이스테이션5 컨트롤러', 'SALE', 75000, '화이트, 정품', 4, 16),

-- 5️⃣ 최유진 (의류 + 취미/게임 + 전자)
(5, 3, '트렌치코트', 'SALE', 78000, '봄 가을용', 5, 18),
(5, 7, '닌텐도 스위치 게임팩 모음', 'SALE', 65000, '3종 세트', 5, 18),
(5, 1, '무선 마우스 로지텍 M650', 'SALE', 29000, '신형, 정품', 5, 18),
(5, 3, '롱패딩 (여성용)', 'SALE', 125000, '겨울 신상', 5, 18),
(5, 7, '보드게임 컬렉션', 'SALE', 42000, '4종 세트', 5, 18),
(5, 1, '갤럭시 워치6', 'SALE', 330000, '골드, 상태 A급', 5, 18),
(5, 3, '블라우스 세트', 'SALE', 42000, '화이트 & 블루 구성', 5, 18),
(5, 5, '전동 마사지건', 'SALE', 89000, '휴대용, 새상품', 5, 18),
(5, 3, '데님 자켓', 'SALE', 49000, '빈티지 디자인', 5, 18),
(5, 1, '애플워치 SE', 'SALE', 270000, '40mm, 실버', 5, 18),

-- 6️⃣ 정은지 (생활용품 + 가구 + 도서)
(6, 5, '청소기 새상품', 'SALE', 98000, '무선 스틱형', 6, 20),
(6, 2, '책장 5단', 'SALE', 85000, '화이트톤, 조립 쉬움', 6, 20),
(6, 4, '정리정돈의 기술', 'SALE', 18000, '생활 실용서', 6, 20),
(6, 2, '소파베드', 'RESERVED', 280000, '2인용, 상태 좋음', 6, 20),
(6, 5, '에어프라이어', 'SALE', 70000, '5L, 미개봉', 6, 20),
(6, 2, '수납장 세트', 'SALE', 125000, '거실용, 튼튼한 재질', 6, 20),
(6, 5, '가습기', 'SALE', 40000, '저소음형', 6, 20),
(6, 4, '인테리어 디자인 북', 'SALE', 25000, '사진 중심 구성', 6, 20),
(6, 2, '사이드 테이블', 'SALE', 55000, '베이지톤', 6, 20),
(6, 5, '스팀다리미', 'SALE', 30000, '소형, 여행용', 6, 20);

-- ==========================================
-- 🌟 판매자 평점 더미 데이터
-- ==========================================
INSERT INTO seller_ratings (seller_id, buyer_id, product_id, rating, comment) VALUES
-- 홍길동(1) 판매자에 대한 평가
(1, 2, 1, 5, '상품 상태가 설명 그대로예요. 잘 쓰고 있습니다.'),
(1, 3, 1, 4, '전반적으로 만족하지만 배송이 조금 늦었어요.'),
(1, 4, 2, 5, '포장도 꼼꼼하고 응대도 친절했습니다.'),
(1, 5, 3, 4, '가격 대비 아주 괜찮아요.'),

-- 김철수(2) 판매자에 대한 평가
(2, 1, 11, 5, '가구 상태가 정말 좋네요. 재구매 의사 있어요.'),
(2, 3, 12, 4, '약간의 사용감은 있지만 전반적으로 만족합니다.'),
(2, 4, 13, 5, '사진보다 실물이 더 예뻐요. 집 분위기가 살았습니다.'),

-- 이영희(3) 판매자에 대한 평가
(3, 1, 21, 5, '도서 상태가 거의 새책 수준입니다.'),
(3, 2, 22, 4, '밑줄이 조금 있었지만 크게 신경 쓰이진 않았어요.'),
(3, 6, 23, 5, '구성이 알차고 배송도 빨랐습니다.'),

-- 박민수(4) 판매자에 대한 평가
(4, 1, 31, 5, '운동기구 튼튼하고 사용감 거의 없어요.'),
(4, 2, 32, 4, '제품은 좋은데 조립 설명서가 없어서 조금 헷갈렸어요.'),
(4, 3, 33, 5, '가격 대비 최고입니다. 홈트에 딱 좋아요.'),

-- 최유진(5) 판매자에 대한 평가
(5, 3, 41, 5, '옷 상태가 완전 새상품 같아요.'),
(5, 4, 42, 4, '사이즈가 살짝 작지만 디자인은 너무 마음에 듭니다.'),
(5, 6, 43, 5, '포장부터 배송까지 모두 만족스러웠어요.'),

-- 정은지(6) 판매자에 대한 평가
(6, 1, 51, 5, '생활용품 퀄리티가 좋고 잘 작동합니다.'),
(6, 2, 52, 4, '사용감은 조금 있지만 기능은 문제없어요.'),
(6, 5, 53, 5, '설명도 자세하고 친절해서 믿고 거래했습니다.');


-- ==========================================
-- 💖 찜 목록 (각 회원 10개씩)
-- ==========================================
INSERT INTO wish_lists (register_id, product_id) VALUES
(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),
(2,21),(2,22),(2,23),(2,24),(2,25),(2,26),(2,27),(2,28),(2,29),(2,30),
(3,31),(3,32),(3,33),(3,34),(3,35),(3,36),(3,37),(3,38),(3,39),(3,40),
(4,41),(4,42),(4,43),(4,44),(4,45),(4,46),(4,47),(4,48),(4,49),(4,50),
(5,51),(5,52),(5,53),(5,54),(5,55),(5,56),(5,57),(5,58),(5,59),(5,60),
(6,1),(6,2),(6,3),(6,4),(6,5),(6,6),(6,7),(6,8),(6,9),(6,10);

-- ==========================================
-- 🖼️ 상품 이미지 매핑 (상품 60개 × 이미지 60개)
-- ==========================================
INSERT INTO product_images (product_id, image_id) VALUES
(1,1),(2,2),(3,3),(4,4),(5,5),(6,6),(7,7),(8,8),(9,9),(10,10),
(11,11),(12,12),(13,13),(14,14),(15,15),(16,16),(17,17),(18,18),(19,19),(20,20),
(21,21),(22,22),(23,23),(24,24),(25,25),(26,26),(27,27),(28,28),(29,29),(30,30),
(31,31),(32,32),(33,33),(34,34),(35,35),(36,36),(37,37),(38,38),(39,39),(40,40),
(41,41),(42,42),(43,43),(44,44),(45,45),(46,46),(47,47),(48,48),(49,49),(50,50),
(51,51),(52,52),(53,53),(54,54),(55,55),(56,56),(57,57),(58,58),(59,59),(60,60);

-- ==========================
-- ✅ 확인용 쿼리
-- ==========================

USE usermarketdb;

SELECT * from user;
select * from products;
select * from product_images;
select * from images;
select * from user_info;
select * from categories;
select * from sido_areas;
select * from sigg_areas;
select * from categories;
SELECT * FROM activity_areas;
SELECT * FROM wish_lists;
