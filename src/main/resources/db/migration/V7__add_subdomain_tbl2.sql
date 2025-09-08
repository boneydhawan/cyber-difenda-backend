CREATE TABLE subdomains (
    id SERIAL PRIMARY KEY,
    scan_id INT REFERENCES scans(id) ON DELETE CASCADE,
    host VARCHAR(255) NOT NULL,
    ips TEXT NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

CREATE TABLE open_ports (
    id SERIAL PRIMARY KEY,
    scan_id INT REFERENCES scans(id) ON DELETE CASCADE,
    port INT NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);