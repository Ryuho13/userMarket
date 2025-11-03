USE usermarketdb;

-- 2) 회원
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
