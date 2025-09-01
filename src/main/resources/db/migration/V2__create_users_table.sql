-- 1. Create Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    uid VARCHAR(128) UNIQUE NOT NULL,   -- Firebase UID
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_by TIMESTAMP DEFAULT NOW(),
    updated_by TIMESTAMP DEFAULT NOW()
);

-- 2. Create Roles table
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL,  -- e.g. admin, reviewer, abstractor
    description TEXT
);

-- 3. Create User_Role mapping table (many-to-many)
CREATE TABLE user_roles (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_by TIMESTAMP DEFAULT NOW(),
    updated_by TIMESTAMP DEFAULT NOW(),
    UNIQUE (user_id, role_id)
);

-- 4. Insert some default roles
INSERT INTO roles (role_name, description) VALUES
('ADMIN', 'System Administrator with full access'),
('ANALYST', 'Reviewer with read review access'),
('SENIOR_ANALYST', 'Reviewer with read/write access')
ON CONFLICT (role_name) DO NOTHING;
