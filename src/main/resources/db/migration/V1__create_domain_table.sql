-- Create Domain Table
CREATE TABLE domain (
    id BIGSERIAL PRIMARY KEY,
    domain_name VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    lastScan VARCHAR(255) NOT NULL,
    issues VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_by TIMESTAMP DEFAULT NOW(),
    updated_by TIMESTAMP DEFAULT NOW()
);

