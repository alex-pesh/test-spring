-- DROP TABLE users;
CREATE TABLE users
(
    id          BIGINT PRIMARY KEY,
    username    VARCHAR(50) UNIQUE,
    "name"      VARCHAR(250),
    email       VARCHAR(250)
);


-- DROP TABLE employee;
-- DROP TABLE department;
-- DROP TABLE job_position;


CREATE TABLE department
(
    id        BIGINT,
    name      VARCHAR(50),
    parent_id BIGINT,

    CONSTRAINT pk_department_id PRIMARY KEY(id),
    CONSTRAINT fk_department_parent_id FOREIGN KEY (parent_id) REFERENCES department
);


CREATE TABLE job_position
(
    id            BIGINT,
    name          VARCHAR(250),
    department_id BIGINT,
    salary        NUMERIC,
    vacation_days BIGINT,

    CONSTRAINT pk_job_position_id PRIMARY KEY(id),
    CONSTRAINT fk_department_id FOREIGN KEY (department_id) REFERENCES department
);


INSERT INTO department VALUES (1, 'Отдел трудоустройства', null);
INSERT INTO department VALUES (2, 'Отдел финансов', null);

-- TRUNCATE job_position;
INSERT INTO job_position
VALUES
    (1, 'Заведующий отделом', 1, 30000, 30),
    (2, 'Заместитель', 1, 25000, 30),
    (3, 'Администратор', 1, 23000, 20),
    (4, 'Бухгалтер', 1, 20000, 20),
    (5, 'Уборщик', 1, 15000, 14)
ON CONFLICT ON CONSTRAINT pk_job_position_id DO NOTHING;

INSERT INTO job_position
VALUES
    (6, 'Заведующий отделом',  2, 50000, 20),
    (7, 'Заместитель', 2, 35000, 20),
    (8, 'Администратор', 2, 25000, 14),
    (9, 'Бухгалтер', 2, 25000, 14),
    (10, 'Уборщик', 2, 15000, 20)
ON CONFLICT ON CONSTRAINT pk_job_position_id DO NOTHING;


-------------------------------------------------------------------------


CREATE TABLE product (
    id      BIGINT NOT NULL,
    version INTEGER NOT NULL,
    name    VARCHAR,
    price   DOUBLE PRECISION NOT NULL,

    CONSTRAINT pk_product_id PRIMARY KEY (id)
);

CREATE SEQUENCE seq_product_id START 1
OWNED BY product.id;
	

INSERT INTO product(id, version, name,  price)
VALUES (nextval('seq_product_id'), 1, 'Полотенце', 250.00);












