CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       login_id VARCHAR(255) NOT NULL,
                       password VARCHAR(255),
                       name VARCHAR(255),
                       email VARCHAR(255),
                       department VARCHAR(255),
                       grade INTEGER,
                       phone VARCHAR(255),
                       user_type VARCHAR(255),
                       role VARCHAR(20),
                       PRIMARY KEY (id),
                       CONSTRAINT UK_login_id UNIQUE (login_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;