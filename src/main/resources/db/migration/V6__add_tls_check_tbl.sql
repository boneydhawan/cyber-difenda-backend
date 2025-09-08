CREATE TABLE scan_tls_security (
    id SERIAL PRIMARY KEY,
    scan_id INT NOT NULL REFERENCES scans(id) ON DELETE CASCADE,
    tls_1_0 BOOLEAN,
    tls_1_1 BOOLEAN,
    tls_1_2 BOOLEAN,
    tls_1_3 BOOLEAN,
    weak_ciphers TEXT,
    hsts_max_age INT,
    missing_headers TEXT,
    http_to_https BOOLEAN,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);