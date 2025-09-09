CREATE TABLE email_security (
    id BIGSERIAL PRIMARY KEY,
    scan_id BIGINT UNIQUE NOT NULL REFERENCES scans(id) ON DELETE CASCADE,
    spf_record TEXT,
    dkim_selector TEXT,
    dmarc_record TEXT,
    adkim CHAR(1),
    aspf CHAR(1),
    rua TEXT,
    mta_sts TEXT,
    tls_rpt TEXT,
    -- Store findings, MX records, and SPF includes as JSON strings
    findings TEXT,
    mx_records TEXT,
    spf_includes TEXT,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_date TIMESTAMP DEFAULT NOW(),
    updated_date TIMESTAMP DEFAULT NOW()
);