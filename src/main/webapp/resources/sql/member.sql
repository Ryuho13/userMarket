CREATE DATABASE userMarketDB;

USE userMarketDB;


CREATE TABLE `users` (
	`id`	BIGINT	NULL,
	`email`	VARCHAR(255)	NOT NULL,
	`password_hash`	VARCHAR(255)	NOT NULL,
	`nickname`	VARCHAR(40)	NOT NULL,
	`profile_img`	VARCHAR(500)	NULL,
	`region_code`	VARCHAR(20)	NULL
);

CREATE TABLE `categories` (
	`id`	INT	NULL,
	`name`	VARCHAR(50)	NOT NULL
);

CREATE TABLE `regions` (
	`code`	VARCHAR(20)	NULL,
	`name`	VARCHAR(100)	NOT NULL,
	`parent`	VARCHAR(100)	NULL
);

CREATE TABLE `products` (
	`id`	BIGINT	NULL,
	`user_id`	BIGINT	NOT NULL,
	`title`	VARCHAR(120)	NOT NULL,
	`description`	TEXT	NOT NULL,
	`price`	INT	NOT NULL,
	`category_id`	INT	NULL,
	`region_code`	VARCHAR(20)	NULL,
	`status`	ENUM('ON_SALE','IN_DEAL','SOLD')	NOT NULL	DEFAULT 'ON_SALE',
	`deleted_at`	DATETIME	NULL,
	`created_at`	DATETIME	NOT NULL,
	`updated_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `product_images` (
	`id`	BIGINT	NULL,
	`product_id`	BIGINT	NOT NULL,
	`created_at`	DATETIME	NOT NULL
);

CREATE TABLE `wishes` (
	`id`	BIGINT	NULL,
	`user_id`	BIGINT	NOT NULL,
	`product_id`	BIGINT	NOT NULL,
	`created_at`	DATETIME	NOT NULL
);

CREATE TABLE `chat_rooms` (
	`id`	BIGINT	NULL,
	`product_id`	BIGINT	NOT NULL,
	`seller_id`	BIGINT	NOT NULL,
	`buyer_id`	BIGINT	NOT NULL,
	`last_msg`	VARCHAR(500)	NULL,
	`last_msg_at`	DATETIME	NULL,
	`unread_seller`	INT	NOT NULL,
	`unread_buyer`	INT	NOT NULL,
	`created_at`	DATETIME	NOT NULL,
	`updated_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `chat_messages` (
	`id`	BIGINT	NULL,
	`room_id`	BIGINT	NOT NULL,
	`sender_id`	BIGINT	NOT NULL,
	`type`	ENUM('TEXT','IMAGE','SYSTEM')	NOT NULL	DEFAULT 'TEXT',
	`content`	TEXT	NOT NULL,
	`is_read`	TINYINT(1)	NOT NULL,
	`created_at`	DATETIME	NOT NULL
);

-- 공통 문자셋/엔진 권장 (이미 그런 상태면 스킵)
ALTER DATABASE used_market CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- 1) users
ALTER TABLE users
  MODIFY id BIGINT NOT NULL AUTO_INCREMENT,
  ADD PRIMARY KEY (id),
  ADD UNIQUE KEY uq_users_email (email),
  ADD UNIQUE KEY uq_users_nickname (nickname),
  MODIFY region_code VARCHAR(20) NULL,
  -- 타임스탬프 기본값 권장
  ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER region_code,
  ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- 2) categories
ALTER TABLE categories
  MODIFY id INT NOT NULL AUTO_INCREMENT,
  ADD PRIMARY KEY (id),
  ADD UNIQUE KEY uq_categories_name (name);

-- 3) regions
ALTER TABLE regions
  MODIFY code VARCHAR(20) NOT NULL,
  ADD PRIMARY KEY (code);

-- 4) products
ALTER TABLE products
  MODIFY id BIGINT NOT NULL AUTO_INCREMENT,
  ADD PRIMARY KEY (id),
  -- 타임스탬프 기본값 정리
  MODIFY created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  MODIFY updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  -- 외래키
  ADD CONSTRAINT fk_products_user
    FOREIGN KEY (user_id) REFERENCES users(id),
  ADD CONSTRAINT fk_products_category
    FOREIGN KEY (category_id) REFERENCES categories(id),
  ADD CONSTRAINT fk_products_region
    FOREIGN KEY (region_code) REFERENCES regions(code);

-- 조회 최적화 인덱스
CREATE INDEX ix_products_region_created   ON products (region_code, created_at DESC);
CREATE INDEX ix_products_category_created ON products (category_id, created_at DESC);
CREATE INDEX ix_products_status_created   ON products (status, created_at DESC);

-- 5) product_images
ALTER TABLE product_images
  MODIFY id BIGINT NOT NULL AUTO_INCREMENT,
  ADD PRIMARY KEY (id),
  ADD CONSTRAINT fk_images_product
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  ADD INDEX ix_images_product_created (product_id, created_at);

-- (선택 권장) 이미지 파일 경로/순서가 필요하면 아래 컬럼 추가
-- ALTER TABLE product_images
--   ADD COLUMN url VARCHAR(500) NOT NULL AFTER product_id,
--   ADD COLUMN sort_order INT NOT NULL DEFAULT 0 AFTER url,
--   ADD INDEX ix_images_product_sort (product_id, sort_order);

-- 6) wishes (찜)
ALTER TABLE wishes
  MODIFY id BIGINT NOT NULL AUTO_INCREMENT,
  ADD PRIMARY KEY (id),
  -- 중복 찜 방지
  ADD CONSTRAINT uq_wishes UNIQUE (user_id, product_id),
  -- 외래키
  ADD CONSTRAINT fk_wishes_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  ADD CONSTRAINT fk_wishes_product
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  -- 타임스탬프 기본값
  MODIFY created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX ix_wishes_user    ON wishes (user_id);
CREATE INDEX ix_wishes_product ON wishes (product_id);

-- 7) chat_rooms
ALTER TABLE chat_rooms
  MODIFY id BIGINT NOT NULL AUTO_INCREMENT,
  ADD PRIMARY KEY (id),
  -- 상품+구매자 1채팅방 제약
  ADD CONSTRAINT uq_chat_room UNIQUE (product_id, buyer_id),
  -- 외래키
  ADD CONSTRAINT fk_room_product FOREIGN KEY (product_id) REFERENCES products(id),
  ADD CONSTRAINT fk_room_seller  FOREIGN KEY (seller_id)  REFERENCES users(id),
  ADD CONSTRAINT fk_room_buyer   FOREIGN KEY (buyer_id)   REFERENCES users(id),
  -- 기본값/타임스탬프
  MODIFY unread_seller INT NOT NULL DEFAULT 0,
  MODIFY unread_buyer  INT NOT NULL DEFAULT 0,
  MODIFY created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  MODIFY updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

CREATE INDEX ix_rooms_seller ON chat_rooms (seller_id, updated_at DESC);
CREATE INDEX ix_rooms_buyer  ON chat_rooms (buyer_id,  updated_at DESC);

-- 8) chat_messages
ALTER TABLE chat_messages
  MODIFY id BIGINT NOT NULL AUTO_INCREMENT,
  ADD PRIMARY KEY (id),
  -- 외래키
  ADD CONSTRAINT fk_msg_room   FOREIGN KEY (room_id)  REFERENCES chat_rooms(id) ON DELETE CASCADE,
  ADD CONSTRAINT fk_msg_sender FOREIGN KEY (sender_id) REFERENCES users(id),
  -- 기본값/인덱스
  MODIFY is_read TINYINT(1) NOT NULL DEFAULT 0,
  MODIFY created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX ix_messages_room_created ON chat_messages (room_id, created_at);
