-- Create Domain Table
CREATE TABLE domain (
    id BIGSERIAL PRIMARY KEY,
    domain_name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL
);

INSERT INTO domain (domain_name, is_active) VALUES
('example.com', true),
('testsite.org', false),
('secureapp.net', true);