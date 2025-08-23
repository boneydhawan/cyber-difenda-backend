-- Create Domain Table
CREATE TABLE domain (
    id BIGSERIAL PRIMARY KEY,
    domain_name VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    lastScan VARCHAR(255) NOT NULL,
    issues VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL
);

-- Insert sample data
INSERT INTO domain (domain_name, status, lastScan, issues, is_active) VALUES
('example.com', 'Warning', '2m ago', '3', true),
('testsite.org', 'Safe', '1h ago', '0', false),
('secureapp.net', 'Critical', '5m ago', '7', true),
('banksecure.io', 'Safe', '10m ago', '1', true),
('myblog.dev', 'Warning', '30m ago', '2', false),
('shopnow.store', 'Safe', '5h ago', '0', true),
('travelbuddy.co', 'Critical', '15m ago', '5', true),
('cybersecure.ai', 'Safe', '20m ago', '0', true);