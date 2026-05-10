CREATE SCHEMA IF NOT EXISTS thesis;

CREATE TABLE thesis.account(
    id BIGINT GENERATED ALWAYS AS IDENTITY CONSTRAINT account_id_pk PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    surname VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL,
    phone VARCHAR(16) NOT NULL,
    date_of_birth DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NULL,
    CONSTRAINT account_email_uq UNIQUE(email),
    CONSTRAINT account_phone_uq UNIQUE(phone)
);

CREATE TABLE thesis.role(
    id BIGINT GENERATED ALWAYS AS IDENTITY CONSTRAINT role_id_pk PRIMARY KEY,
    code VARCHAR(16) NOT NULL,
    description VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NULL,
    CONSTRAINT role_code_uq UNIQUE(code)
);

CREATE TABLE thesis.account_role(
    id BIGINT GENERATED ALWAYS AS IDENTITY CONSTRAINT account_role_id_pk PRIMARY KEY,
    account_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NULL,
    CONSTRAINT account_role_user_account_id_role_id_uq UNIQUE(account_id, role_id),
    CONSTRAINT account_role_user_fk FOREIGN KEY(account_id) REFERENCES thesis.account(id),
    CONSTRAINT account_role_role_fk FOREIGN KEY(role_id) REFERENCES thesis.role(id)
);