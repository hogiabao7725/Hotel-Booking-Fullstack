-- Table: features
CREATE TABLE features
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(55)  NOT NULL UNIQUE,
    icon_url    VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: facilities
CREATE TABLE facilities
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    icon_url    VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: profiles (Add avatar_url column to existing profiles table created in V1)
ALTER TABLE profiles
    ADD COLUMN avatar_url VARCHAR(255) DEFAULT NULL;
