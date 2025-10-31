USE usermarketdb;

-- 1) 지역
CREATE TABLE region (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  city         VARCHAR(30)     NOT NULL,
  district     VARCHAR(30)     NOT NULL,
  neighborhood VARCHAR(30)     NOT NULL,
  UNIQUE KEY uk_region_3 (city, district, neighborhood)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2) 회원
CREATE TABLE user (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  account_id  VARCHAR(30)  NOT NULL,      -- 로그인 아이디
  pw          VARCHAR(255) NOT NULL,      -- 비밀번호 해시
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
  id           INT AUTO_INCREMENT PRIMARY KEY,
  u_id         INT NOT NULL,              -- FK to user.id (1:1)
  nickname     VARCHAR(30) NOT NULL,
  profile_img  VARCHAR(255) NULL,
  intro        VARCHAR(255) NULL,
  region_id    INT NULL,                  -- FK to region.id
  addr_detail  VARCHAR(100) NULL,

  UNIQUE KEY uk_userinfo_uid (u_id),      -- 1:1 보장
  UNIQUE KEY uk_userinfo_nick (nickname),

  CONSTRAINT fk_userinfo_user
    FOREIGN KEY (u_id) REFERENCES user(id)
    ON DELETE CASCADE ON UPDATE RESTRICT,

  CONSTRAINT fk_userinfo_region
    FOREIGN KEY (region_id) REFERENCES region(id)
    ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
