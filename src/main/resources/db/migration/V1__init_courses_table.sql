CREATE TABLE courses (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         course_code VARCHAR(20) NOT NULL UNIQUE,
                         title VARCHAR(100) NOT NULL,
                         professor_name VARCHAR(50),
                         credits INT NOT NULL,
                         grade_level INT NOT NULL,
                         category VARCHAR(30),
                         track_name VARCHAR(100),
                         description TEXT,
                         syllabus_url VARCHAR(500),
                         recommendation INT,
                         pos_x FLOAT,
                         pos_y FLOAT
);

CREATE TABLE prerequisites (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               pre_id BIGINT NOT NULL,
                               post_id BIGINT NOT NULL,
                               UNIQUE KEY uq_pre_post (pre_id, post_id),
                               CONSTRAINT fk_prerequisites_post_course
                                   FOREIGN KEY (post_id) REFERENCES courses (id)
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_prerequisites_pre_course
                                   FOREIGN KEY (pre_id) REFERENCES courses (id)
                                       ON DELETE CASCADE
);