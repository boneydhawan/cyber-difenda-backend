-- Scan history table
CREATE TABLE scans (
    id SERIAL PRIMARY KEY,
    assessment_id INT NOT NULL REFERENCES assessment(id) ON DELETE CASCADE,
    scan_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50), -- running, completed, failed
    summary_findings VARCHAR(50),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);

-- DNS records for each scan
CREATE TABLE dns_records (
    id SERIAL PRIMARY KEY,
    scan_id INT NOT NULL REFERENCES scans(id) ON DELETE CASCADE,
    record_type VARCHAR(10) NOT NULL, -- A, AAAA, MX, TXT, NS, SOA, DS
    record_value TEXT NOT NULL,
    last_checked TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);