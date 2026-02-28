CREATE TABLE users
(
    id       INTEGER PRIMARY KEY    NOT NULL,
    username CHARACTER VARYING(100) NOT NULL,
    name     CHARACTER VARYING(250) NOT NULL,
    email    CHARACTER VARYING(250) NOT NULL
);


CREATE TABLE department
(
    id     INTEGER PRIMARY KEY    NOT NULL,
    name   CHARACTER VARYING(250) NOT NULL,
    parent_id INTEGER,

    CONSTRAINT fk_department_parent FOREIGN KEY (parent_id) REFERENCES department (id)
);


CREATE TABLE job_position
(
    id            INTEGER PRIMARY KEY    NOT NULL,
    name          CHARACTER VARYING(250) NOT NULL,
    department_id INTEGER                NOT NULL,
    salary        DOUBLE PRECISION,
    vacation_days INTEGER,

    CONSTRAINT fk_department_id FOREIGN KEY (department_id) REFERENCES department (id)
);


INSERT INTO department
VALUES
    (1, 'Администрация', null),
    (2, 'Бухгалтерия', null),
    (3, 'Отдел трудоустройства', null),
    (4, 'Служба безопаснсти', null),
    (5, 'Технический персонал', null);


INSERT INTO job_position
VALUES
    (1, 'Директор', 1, 100000.0, 30),
    (2, 'Заместитель директора', 1, 80000.0, 30),
    (3, 'Главный бухгалтер', 2, 80000.0, 25),
    (4, 'Бухгалтер', 2, 80000.0, 25),
    (6, 'Инженер по безопасности', 4, 80000.0, 20),
    (7, 'Инструктор по безопасности', 4, 40000.0, 25),
    (8, 'Монтажник', 5, 40000.0, 20),
    (9, 'Слесарь', 5, 40000.0, 20);


INSERT INTO users
VALUES
    (1, 'user', 'User', 'user@mail.test.ru');