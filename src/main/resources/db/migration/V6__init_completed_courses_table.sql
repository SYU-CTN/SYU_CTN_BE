CREATE TABLE completed_courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(255) NOT NULL,
    course_id BIGINT NOT NULL,
    grade VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_completed_course_user_course (login_id, course_id),
    CONSTRAINT fk_completed_courses_login_id
        FOREIGN KEY (login_id) REFERENCES users (login_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_completed_courses_course_id
        FOREIGN KEY (course_id) REFERENCES courses (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
