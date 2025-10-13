INSERT INTO agent (id, name, status) VALUES
    ('agent-001', 'Hospital Alpha Data Center', 'ACTIVE'),
    ('agent-002', 'Research Institute Beta', 'ACTIVE'),
    ('agent-003', 'Medical Center Gamma', 'INACTIVE'),
    ('agent-004', 'Biobank Delta Repository', 'ACTIVE'),
    ('agent-005', 'Clinical Data Hub Epsilon', 'ACTIVE');

-- Insert dummy quality checks
-- NOTE: Result values represent the percentage/count of INVALID records (0.0 = perfect, 100.0 = all invalid)
-- warning_threshold: if result > warning_threshold, trigger warning
-- error_threshold: if result > error_threshold, trigger error
INSERT INTO quality_check (hash, name, description, registered_at, warning_threshold, error_threshold) VALUES
    ('unsupported-gender-check', 'Unsupported Gender Values', 'Percentage of patients with non-supported gender attribute values (not Male/Female/Other/Unknown)', '2024-01-15 10:00:00', 5.0, 15.0),
    ('missing-birthdate-check', 'Missing Birth Date', 'Percentage of patients with missing or null birth date values', '2024-01-15 10:15:00', 3.0, 10.0),
    ('invalid-date-check', 'Invalid Date Values', 'Percentage of records with logically invalid dates (e.g., future birth dates, death before birth)', '2024-01-15 10:30:00', 2.0, 8.0),
    ('duplicate-patient-check', 'Duplicate Patient Records', 'Percentage of patients that appear to be duplicates based on matching identifiers', '2024-01-15 10:45:00', 1.0, 5.0),
    ('invalid-format-check', 'Invalid Data Formats', 'Percentage of records with data that does not follow expected formats (e.g., malformed IDs, invalid postal codes)', '2024-01-15 11:00:00', 4.0, 12.0),
    ('broken-reference-check', 'Broken Reference Integrity', 'Percentage of records with references to non-existent related entities', '2024-01-15 11:15:00', 2.0, 8.0),
    ('outlier-value-check', 'Statistical Outlier Values', 'Percentage of numerical values that are statistical outliers (e.g., age > 150, negative measurements)', '2024-01-15 11:30:00', 10.0, 25.0),
    ('invalid-coding-check', 'Invalid Medical Codes', 'Percentage of records with invalid or non-standard medical codes (ICD-10, SNOMED CT)', '2024-01-15 11:45:00', 3.0, 10.0);

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
    ('report-001', 'unsupported-gender-check', 2.0),
    ('report-001', 'missing-birthdate-check', 1.0),
    ('report-001', 'invalid-date-check', 1.0),
    ('report-001', 'duplicate-patient-check', 0.0),
    ('report-001', 'invalid-format-check', 3.0),
    ('report-001', 'broken-reference-check', 1.0),
    ('report-001', 'outlier-value-check', 8.0),
    ('report-001', 'invalid-coding-check', 2.0);

-- Report 2 results (mixed quality - some warnings)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-002', 'unsupported-gender-check', 7.0),
    ('report-002', 'missing-birthdate-check', 5.0),
    ('report-002', 'invalid-date-check', 3.0),
    ('report-002', 'duplicate-patient-check', 2.0),
    ('report-002', 'invalid-format-check', 9.0),
    ('report-002', 'broken-reference-check', 4.0),
    ('report-002', 'outlier-value-check', 15.0),
    ('report-002', 'invalid-coding-check', 6.0);

-- Report 3 results (poor quality data - many errors above thresholds)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-003', 'unsupported-gender-check', 18.0),
    ('report-003', 'missing-birthdate-check', 12.0),
    ('report-003', 'invalid-date-check', 9.0),
    ('report-003', 'duplicate-patient-check', 6.0),
    ('report-003', 'invalid-format-check', 14.0),
    ('report-003', 'broken-reference-check', 11.0),
    ('report-003', 'outlier-value-check', 28.0),
    ('report-003', 'invalid-coding-check', 13.0);

-- Report 4 results (excellent quality - very low error rates)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-004', 'unsupported-gender-check', 0.0),
    ('report-004', 'missing-birthdate-check', 1.0),
    ('report-004', 'invalid-date-check', 0.0),
    ('report-004', 'duplicate-patient-check', 0.0),
    ('report-004', 'invalid-format-check', 1.0),
    ('report-004', 'broken-reference-check', 0.0),
    ('report-004', 'outlier-value-check', 5.0),
    ('report-004', 'invalid-coding-check', 1.0);

-- Report 5 results (average quality)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-005', 'unsupported-gender-check', 6.0),
    ('report-005', 'missing-birthdate-check', 4.0),
    ('report-005', 'invalid-date-check', 3.0),
    ('report-005', 'duplicate-patient-check', 2.0),
    ('report-005', 'invalid-format-check', 7.0),
    ('report-005', 'broken-reference-check', 3.0),
    ('report-005', 'outlier-value-check', 12.0),
    ('report-005', 'invalid-coding-check', 5.0);

-- Add more results for remaining reports with varying quality scores
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    -- Report 6 (good quality)
    ('report-006', 'unsupported-gender-check', 2.0),
    ('report-006', 'missing-birthdate-check', 2.0),
    ('report-006', 'invalid-date-check', 1.0),
    ('report-006', 'duplicate-patient-check', 1.0),

    -- Report 7 (poor quality - warnings)
    ('report-007', 'unsupported-gender-check', 11.0),
    ('report-007', 'missing-birthdate-check', 8.0),
    ('report-007', 'invalid-format-check', 10.0),
    ('report-007', 'outlier-value-check', 18.0),

    -- Report 8 (good quality)
    ('report-008', 'unsupported-gender-check', 3.0),
    ('report-008', 'missing-birthdate-check', 2.0),
    ('report-008', 'invalid-date-check', 2.0),
    ('report-008', 'duplicate-patient-check', 1.0),
    ('report-008', 'broken-reference-check', 2.0),

    -- Report 9 (very poor quality - many errors)
    ('report-009', 'unsupported-gender-check', 19.0),
    ('report-009', 'missing-birthdate-check', 15.0),
    ('report-009', 'invalid-format-check', 16.0),
    ('report-009', 'invalid-coding-check', 14.0),

    -- Report 10 (excellent quality)
    ('report-010', 'unsupported-gender-check', 1.0),
    ('report-010', 'missing-birthdate-check', 1.0),
    ('report-010', 'invalid-date-check', 1.0),
    ('report-010', 'duplicate-patient-check', 0.0),
    ('report-010', 'invalid-format-check', 2.0),
    ('report-010', 'broken-reference-check', 1.0),
    ('report-010', 'outlier-value-check', 6.0),
    ('report-010', 'invalid-coding-check', 2.0);

-- Add some recent reports with current timestamps for immediate testing
INSERT INTO report (id, timestamp, agent_id) VALUES
    ('report-current-1', CURRENT_TIMESTAMP - INTERVAL '2' HOUR, 'agent-001'),
    ('report-current-2', CURRENT_TIMESTAMP - INTERVAL '1' HOUR, 'agent-002'),
    ('report-current-3', CURRENT_TIMESTAMP - INTERVAL '30' MINUTE, 'agent-004');

-- Add results for current reports
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-current-1', 'unsupported-gender-check', 4.0),
    ('report-current-1', 'missing-birthdate-check', 2.0),
    ('report-current-1', 'duplicate-patient-check', 1.0),
    ('report-current-2', 'unsupported-gender-check', 9.0),
    ('report-current-2', 'invalid-format-check', 11.0),
    ('report-current-2', 'invalid-coding-check', 8.0),
    ('report-current-3', 'unsupported-gender-check', 1.0),
    ('report-current-3', 'missing-birthdate-check', 2.0),
    ('report-current-3', 'invalid-date-check', 2.0),
    ('report-current-3', 'broken-reference-check', 1.0);
