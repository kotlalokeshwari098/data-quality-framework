-- Create quality_check table
CREATE TABLE quality_check (
    hash VARCHAR(255) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    registered_at TIMESTAMP NOT NULL,
    warning_threshold DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    error_threshold DOUBLE PRECISION NOT NULL DEFAULT 0.0
);

-- Create quality_check_result table (join table with result data)
CREATE TABLE quality_check_result (
    report_id VARCHAR(36) NOT NULL,
    quality_check_hash VARCHAR(255) NOT NULL,
    result DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (report_id, quality_check_hash),
    CONSTRAINT fk_result_report FOREIGN KEY (report_id) REFERENCES report(id) ON DELETE CASCADE,
    CONSTRAINT fk_result_quality_check FOREIGN KEY (quality_check_hash) REFERENCES quality_check(hash) ON DELETE CASCADE
);

-- Add indexes for better query performance
CREATE INDEX idx_quality_check_name ON quality_check(name);
CREATE INDEX idx_quality_check_registered_at ON quality_check(registered_at);
CREATE INDEX idx_result_quality_check_hash ON quality_check_result(quality_check_hash);
