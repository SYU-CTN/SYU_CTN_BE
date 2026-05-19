CREATE TABLE courses (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         course_code VARCHAR(255) NOT NULL UNIQUE,
                         title VARCHAR(255) NOT NULL,
                         credits INT,
                         grade_level INT,
                         category VARCHAR(255),
                         description TEXT,
                         syllabus_url VARCHAR(255),
                         recommendation BIGINT
);