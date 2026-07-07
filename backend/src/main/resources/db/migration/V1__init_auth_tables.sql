-- Table: roles
-- Description: Stores system roles (Admin, Customer)
CREATE TABLE roles
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(100)
);

-- Table: accounts
-- Description: Core credentials for authentication and lifecycle management
CREATE TABLE accounts
(
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id       BIGINT       NOT NULL REFERENCES roles (id),
    status        INTEGER                  DEFAULT 0,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
COMMENT
ON COLUMN accounts.status IS '0: Pending activation, 1: Active, 2: Banned, 3: Deleted';

-- Table: profiles
-- Description: Personal information mapped 1:1 with accounts for all roles
CREATE TABLE profiles
(
    id          BIGSERIAL PRIMARY KEY,
    account_id  BIGINT       NOT NULL UNIQUE REFERENCES accounts (id) ON DELETE CASCADE,
    full_name   VARCHAR(100) NOT NULL,
    phone       VARCHAR(20) UNIQUE,
    id_card     VARCHAR(20)              DEFAULT NULL, -- National ID/Passport (Required for customers at check-in)
    gender      VARCHAR(10)              DEFAULT NULL,
    nationality VARCHAR(50)              DEFAULT 'Vietnam',
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: refresh_tokens
-- Description: Manages JWT Refresh Token lifecycle for extended sessions
CREATE TABLE refresh_tokens
(
    id          BIGSERIAL PRIMARY KEY,
    account_id  BIGINT                   NOT NULL UNIQUE REFERENCES accounts (id) ON DELETE CASCADE,
    token       VARCHAR(500)             NOT NULL UNIQUE,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO roles (id, name, description)
VALUES (1, 'ROLE_ADMIN', 'System Administrator'),
       (2, 'ROLE_CUSTOMER', 'Hotel Customer');

SELECT setval(pg_get_serial_sequence('roles', 'id'), coalesce(max(id), 0) + 1, false)
FROM roles;