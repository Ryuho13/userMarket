CREATE DATABASE IF NOT EXISTS market_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE market_db;

-- 카테고리
CREATE TABLE categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(20) NOT NULL
);

-- 시도 지역
CREATE TABLE sido_areas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  adm_code VARCHAR(2) NOT NULL COMMENT 'BTREE IDX',
  name VARCHAR(50) NOT NULL COMMENT 'GIN IDX',
  version VARCHAR(20) NOT NULL
);

-- 시군구 지역
CREATE TABLE sigg_areas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  sido_area_id INT NOT NULL,
  adm_code VARCHAR(5) NOT NULL COMMENT 'BTREE IDX',
  name VARCHAR(50) NOT NULL,
  version TIMESTAMP NOT NULL,
  FOREIGN KEY (sido_area_id) REFERENCES sido_areas(id)
);

-- 사용자
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  mobile_number VARCHAR(11) NOT NULL UNIQUE,
  activated BOOLEAN NOT NULL DEFAULT TRUE,
  rating_score DECIMAL(3,1) NOT NULL DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 활동 지역
CREATE TABLE activity_areas (
  user_id BIGINT NOT NULL,
  id2 INT NOT NULL,
  distance_meters SMALLINT NOT NULL,
  emd_area_ids JSON NOT NULL COMMENT 'GIN IDX',
  authenticated_at TIMESTAMP NULL,
  PRIMARY KEY (user_id, id2),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (id2) REFERENCES sigg_areas(id)
);

-- 파일
CREATE TABLE imgs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  uploader_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (uploader_id) REFERENCES users(id)
);

-- 상품
CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  seller_id BIGINT NOT NULL COMMENT 'BTREE IDX',
  category_id INT NOT NULL COMMENT 'GIN IDX',
  title VARCHAR(100) NOT NULL COMMENT 'GIN IDX (TRIGRAM)',
  status ENUM('SALE', 'RESERVED', 'SOLD_OUT') NOT NULL DEFAULT 'SALE',
  sell_price INT NULL,
  view_count INT NOT NULL DEFAULT 0,
  description TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (seller_id) REFERENCES users(id),
  FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 상품 이미지
CREATE TABLE products_images (
  products_id BIGINT NOT NULL,
  img_id BIGINT NOT NULL,
  PRIMARY KEY (products_id, img_id),
  FOREIGN KEY (products_id) REFERENCES products(id) ON DELETE CASCADE,
  FOREIGN KEY (img_id) REFERENCES imgs(id)
);

-- 팔로우
CREATE TABLE follow_users (
  user_id BIGINT NOT NULL,
  target_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, target_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (target_id) REFERENCES users(id)
);

-- 찜 목록
CREATE TABLE wish_lists (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  register_id BIGINT NOT NULL,
  products_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (register_id) REFERENCES users(id),
  FOREIGN KEY (products_id) REFERENCES products(id)
);

-- 채팅방
CREATE TABLE chat_room (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  products_id BIGINT NOT NULL,
  buyer_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (products_id) REFERENCES products(id),
  FOREIGN KEY (buyer_id) REFERENCES users(id)
);

-- 채팅 메시지
CREATE TABLE chat_messages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  chat_room_id BIGINT NOT NULL,
  sender_id BIGINT NULL,
  message VARCHAR(500) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (chat_room_id) REFERENCES chat_room(id),
  FOREIGN KEY (sender_id) REFERENCES users(id)
);

USE market_db;
