
DROP TABLE IF EXISTS domian;

-- Create Domain Table
CREATE TABLE assessment (
    id BIGSERIAL PRIMARY KEY,
    domain VARCHAR(255) NOT NULL,
    organization VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    last_scan VARCHAR(255) NOT NULL,
    issues VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

