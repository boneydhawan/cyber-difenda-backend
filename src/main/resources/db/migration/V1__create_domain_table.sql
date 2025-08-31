-- Create Domain Table
CREATE TABLE domain (
    id BIGSERIAL PRIMARY KEY,
    domain_name VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    lastScan VARCHAR(255) NOT NULL,
    issues VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL
);

