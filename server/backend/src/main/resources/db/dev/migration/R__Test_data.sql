INSERT INTO agent (id, name, status) VALUES
    ('agent-001', 'Hospital Alpha Data Center', 'ACTIVE'),
    ('agent-002', 'Research Institute Beta', 'ACTIVE'),
    ('agent-003', 'Medical Center Gamma', 'INACTIVE'),
    ('agent-004', 'Biobank Delta Repository', 'ACTIVE'),
    ('agent-005', 'Clinical Data Hub Epsilon', 'ACTIVE');

-- Insert dummy quality checks
-- NOTE: Result values represent the percentage/count of INVALID records (0.0 = perfect, 1.0 = all invalid)
-- warning_threshold: if result > warning_threshold, trigger warning
-- error_threshold: if result > error_threshold, trigger error
INSERT INTO quality_check (hash, name, description, registered_at, warning_threshold, error_threshold) VALUES
    ('unsupported-gender-check', 'Unsupported Gender Values', 'Percentage of patients with non-supported gender attribute values (not Male/Female/Other/Unknown)', '2024-01-15 10:00:00', 0.05, 0.15),
    ('missing-birthdate-check', 'Missing Birth Date', 'Percentage of patients with missing or null birth date values', '2024-01-15 10:15:00', 0.03, 0.10),
    ('invalid-date-check', 'Invalid Date Values', 'Percentage of records with logically invalid dates (e.g., future birth dates, death before birth)', '2024-01-15 10:30:00', 0.02, 0.08),
    ('duplicate-patient-check', 'Duplicate Patient Records', 'Percentage of patients that appear to be duplicates based on matching identifiers', '2024-01-15 10:45:00', 0.01, 0.05),
    ('invalid-format-check', 'Invalid Data Formats', 'Percentage of records with data that does not follow expected formats (e.g., malformed IDs, invalid postal codes)', '2024-01-15 11:00:00', 0.04, 0.12),
    ('broken-reference-check', 'Broken Reference Integrity', 'Percentage of records with references to non-existent related entities', '2024-01-15 11:15:00', 0.02, 0.08),
    ('outlier-value-check', 'Statistical Outlier Values', 'Percentage of numerical values that are statistical outliers (e.g., age > 150, negative measurements)', '2024-01-15 11:30:00', 0.10, 0.25),
    ('invalid-coding-check', 'Invalid Medical Codes', 'Percentage of records with invalid or non-standard medical codes (ICD-10, SNOMED CT)', '2024-01-15 11:45:00', 0.03, 0.10);

-- Insert dummy reports for the past 30 days
INSERT INTO report (id, timestamp, agent_id) VALUES
    ('report-001', '2024-10-01 09:00:00', 'agent-001'),
    ('report-002', '2024-10-01 14:30:00', 'agent-002'),
    ('report-003', '2024-10-02 08:15:00', 'agent-001'),
    ('report-004', '2024-10-02 16:45:00', 'agent-004'),
    ('report-005', '2024-10-03 10:20:00', 'agent-002'),
    ('report-006', '2024-10-03 13:10:00', 'agent-005'),
    ('report-007', '2024-10-04 11:30:00', 'agent-001'),
    ('report-008', '2024-10-04 15:20:00', 'agent-004'),
    ('report-009', '2024-10-05 09:45:00', 'agent-002'),
    ('report-010', '2024-10-05 14:15:00', 'agent-005'),
    ('report-011', '2024-10-06 10:00:00', 'agent-001'),
    ('report-012', '2024-10-06 16:30:00', 'agent-004'),
    ('report-013', '2024-10-07 08:45:00', 'agent-002'),
    ('report-014', '2024-10-07 12:20:00', 'agent-005'),
    ('report-015', '2024-10-08 11:15:00', 'agent-001'),
    ('report-016', '2024-10-08 15:40:00', 'agent-004'),
    ('report-017', '2024-10-09 09:30:00', 'agent-002'),
    ('report-018', '2024-10-09 14:50:00', 'agent-005'),
    ('report-019', '2024-10-10 10:45:00', 'agent-001'),
    ('report-020', '2024-10-10 16:10:00', 'agent-004');

-- Insert quality check results for the reports
-- Report 1 results (good quality data - low error rates)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-001', 'unsupported-gender-check', 0.02),
    ('report-001', 'missing-birthdate-check', 0.01),
    ('report-001', 'invalid-date-check', 0.01),
    ('report-001', 'duplicate-patient-check', 0.00),
    ('report-001', 'invalid-format-check', 0.03),
    ('report-001', 'broken-reference-check', 0.01),
    ('report-001', 'outlier-value-check', 0.08),
    ('report-001', 'invalid-coding-check', 0.02);

-- Report 2 results (mixed quality - some warnings)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-002', 'unsupported-gender-check', 0.07),
    ('report-002', 'missing-birthdate-check', 0.05),
    ('report-002', 'invalid-date-check', 0.03),
    ('report-002', 'duplicate-patient-check', 0.02),
    ('report-002', 'invalid-format-check', 0.09),
    ('report-002', 'broken-reference-check', 0.04),
    ('report-002', 'outlier-value-check', 0.15),
    ('report-002', 'invalid-coding-check', 0.06);

-- Report 3 results (poor quality data - many errors above thresholds)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-003', 'unsupported-gender-check', 0.18),
    ('report-003', 'missing-birthdate-check', 0.12),
    ('report-003', 'invalid-date-check', 0.09),
    ('report-003', 'duplicate-patient-check', 0.06),
    ('report-003', 'invalid-format-check', 0.14),
    ('report-003', 'broken-reference-check', 0.11),
    ('report-003', 'outlier-value-check', 0.28),
    ('report-003', 'invalid-coding-check', 0.13);

-- Report 4 results (excellent quality - very low error rates)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-004', 'unsupported-gender-check', 0.00),
    ('report-004', 'missing-birthdate-check', 0.01),
    ('report-004', 'invalid-date-check', 0.00),
    ('report-004', 'duplicate-patient-check', 0.00),
    ('report-004', 'invalid-format-check', 0.01),
    ('report-004', 'broken-reference-check', 0.00),
    ('report-004', 'outlier-value-check', 0.05),
    ('report-004', 'invalid-coding-check', 0.01);

-- Report 5 results (average quality)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-005', 'unsupported-gender-check', 0.06),
    ('report-005', 'missing-birthdate-check', 0.04),
    ('report-005', 'invalid-date-check', 0.03),
    ('report-005', 'duplicate-patient-check', 0.02),
    ('report-005', 'invalid-format-check', 0.07),
    ('report-005', 'broken-reference-check', 0.03),
    ('report-005', 'outlier-value-check', 0.12),
    ('report-005', 'invalid-coding-check', 0.05);

-- Add more results for remaining reports with varying quality scores
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    -- Report 6 (good quality)
    ('report-006', 'unsupported-gender-check', 0.02),
    ('report-006', 'missing-birthdate-check', 0.02),
    ('report-006', 'invalid-date-check', 0.01),
    ('report-006', 'duplicate-patient-check', 0.01),

    -- Report 7 (poor quality - warnings)
    ('report-007', 'unsupported-gender-check', 0.11),
    ('report-007', 'missing-birthdate-check', 0.08),
    ('report-007', 'invalid-format-check', 0.10),
    ('report-007', 'outlier-value-check', 0.18),

    -- Report 8 (good quality)
    ('report-008', 'unsupported-gender-check', 0.03),
    ('report-008', 'missing-birthdate-check', 0.02),
    ('report-008', 'invalid-date-check', 0.02),
    ('report-008', 'duplicate-patient-check', 0.01),
    ('report-008', 'broken-reference-check', 0.02),

    -- Report 9 (very poor quality - many errors)
    ('report-009', 'unsupported-gender-check', 0.19),
    ('report-009', 'missing-birthdate-check', 0.15),
    ('report-009', 'invalid-format-check', 0.16),
    ('report-009', 'invalid-coding-check', 0.14),

    -- Report 10 (excellent quality)
    ('report-010', 'unsupported-gender-check', 0.01),
    ('report-010', 'missing-birthdate-check', 0.01),
    ('report-010', 'invalid-date-check', 0.01),
    ('report-010', 'duplicate-patient-check', 0.00),
    ('report-010', 'invalid-format-check', 0.02),
    ('report-010', 'broken-reference-check', 0.01),
    ('report-010', 'outlier-value-check', 0.06),
    ('report-010', 'invalid-coding-check', 0.02);

-- Add some recent reports with current timestamps for immediate testing
INSERT INTO report (id, timestamp, agent_id) VALUES
    ('report-current-1', CURRENT_TIMESTAMP - INTERVAL '2' HOUR, 'agent-001'),
    ('report-current-2', CURRENT_TIMESTAMP - INTERVAL '1' HOUR, 'agent-002'),
    ('report-current-3', CURRENT_TIMESTAMP - INTERVAL '30' MINUTE, 'agent-004');

-- Add results for current reports
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-current-1', 'unsupported-gender-check', 0.04),
    ('report-current-1', 'missing-birthdate-check', 0.02),
    ('report-current-1', 'duplicate-patient-check', 0.01),
    ('report-current-2', 'unsupported-gender-check', 0.09),
    ('report-current-2', 'invalid-format-check', 0.11),
    ('report-current-2', 'invalid-coding-check', 0.08),
    ('report-current-3', 'unsupported-gender-check', 0.01),
    ('report-current-3', 'missing-birthdate-check', 0.02),
    ('report-current-3', 'invalid-date-check', 0.02),
    ('report-current-3', 'broken-reference-check', 0.01);
