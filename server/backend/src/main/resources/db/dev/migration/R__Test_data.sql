INSERT INTO agent (id, name, status) VALUES
    ('agent-001', 'Hospital Alpha Data Center', 'ACTIVE'),
    ('agent-002', 'Research Institute Beta', 'ACTIVE'),
    ('agent-003', 'Medical Center Gamma', 'INACTIVE'),
    ('agent-004', 'Biobank Delta Repository', 'ACTIVE'),
    ('agent-005', 'Clinical Data Hub Epsilon', 'ACTIVE');

-- Insert dummy quality checks
INSERT INTO quality_check (hash, name, description, registered_at, warning_threshold, error_threshold) VALUES
    ('patient-count-check', 'Patient Count Validation', 'Validates that the patient count is within expected ranges', '2024-01-15 10:00:00', 0.8, 0.5),
    ('data-completeness-check', 'Data Completeness Check', 'Ensures that required fields are populated for patient records', '2024-01-15 10:15:00', 0.9, 0.7),
    ('date-consistency-check', 'Date Consistency Validation', 'Verifies that dates are logical and consistent across records', '2024-01-15 10:30:00', 0.85, 0.6),
    ('duplicate-detection-check', 'Duplicate Record Detection', 'Identifies potential duplicate patient records in the system', '2024-01-15 10:45:00', 0.95, 0.8),
    ('format-validation-check', 'Data Format Validation', 'Validates that data follows expected formats and standards', '2024-01-15 11:00:00', 0.88, 0.65),
    ('reference-integrity-check', 'Reference Integrity Check', 'Ensures referential integrity between related data entities', '2024-01-15 11:15:00', 0.92, 0.75),
    ('outlier-detection-check', 'Statistical Outlier Detection', 'Identifies statistical outliers in numerical data fields', '2024-01-15 11:30:00', 0.7, 0.4),
    ('coding-standard-check', 'Medical Coding Standards', 'Validates compliance with medical coding standards (ICD-10, SNOMED)', '2024-01-15 11:45:00', 0.9, 0.7);

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
-- Report 1 results (good quality data)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-001', 'patient-count-check', 0.95),
    ('report-001', 'data-completeness-check', 0.92),
    ('report-001', 'date-consistency-check', 0.88),
    ('report-001', 'duplicate-detection-check', 0.97),
    ('report-001', 'format-validation-check', 0.90),
    ('report-001', 'reference-integrity-check', 0.94),
    ('report-001', 'outlier-detection-check', 0.75),
    ('report-001', 'coding-standard-check', 0.89);

-- Report 2 results (mixed quality)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-002', 'patient-count-check', 0.82),
    ('report-002', 'data-completeness-check', 0.76),
    ('report-002', 'date-consistency-check', 0.85),
    ('report-002', 'duplicate-detection-check', 0.91),
    ('report-002', 'format-validation-check', 0.73),
    ('report-002', 'reference-integrity-check', 0.89),
    ('report-002', 'outlier-detection-check', 0.68),
    ('report-002', 'coding-standard-check', 0.81);

-- Report 3 results (poor quality data - some below thresholds)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-003', 'patient-count-check', 0.45),
    ('report-003', 'data-completeness-check', 0.62),
    ('report-003', 'date-consistency-check', 0.71),
    ('report-003', 'duplicate-detection-check', 0.83),
    ('report-003', 'format-validation-check', 0.58),
    ('report-003', 'reference-integrity-check', 0.69),
    ('report-003', 'outlier-detection-check', 0.35),
    ('report-003', 'coding-standard-check', 0.64);

-- Report 4 results (excellent quality)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-004', 'patient-count-check', 0.98),
    ('report-004', 'data-completeness-check', 0.96),
    ('report-004', 'date-consistency-check', 0.94),
    ('report-004', 'duplicate-detection-check', 0.99),
    ('report-004', 'format-validation-check', 0.95),
    ('report-004', 'reference-integrity-check', 0.97),
    ('report-004', 'outlier-detection-check', 0.85),
    ('report-004', 'coding-standard-check', 0.93);

-- Report 5 results (average quality)
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-005', 'patient-count-check', 0.79),
    ('report-005', 'data-completeness-check', 0.84),
    ('report-005', 'date-consistency-check', 0.81),
    ('report-005', 'duplicate-detection-check', 0.88),
    ('report-005', 'format-validation-check', 0.77),
    ('report-005', 'reference-integrity-check', 0.85),
    ('report-005', 'outlier-detection-check', 0.62),
    ('report-005', 'coding-standard-check', 0.80);

-- Add more results for remaining reports with varying quality scores
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    -- Report 6
    ('report-006', 'patient-count-check', 0.91),
    ('report-006', 'data-completeness-check', 0.87),
    ('report-006', 'date-consistency-check', 0.93),
    ('report-006', 'duplicate-detection-check', 0.95),

    -- Report 7
    ('report-007', 'patient-count-check', 0.67),
    ('report-007', 'data-completeness-check', 0.74),
    ('report-007', 'format-validation-check', 0.71),
    ('report-007', 'outlier-detection-check', 0.58),

    -- Report 8
    ('report-008', 'patient-count-check', 0.89),
    ('report-008', 'data-completeness-check', 0.92),
    ('report-008', 'date-consistency-check', 0.86),
    ('report-008', 'duplicate-detection-check', 0.94),
    ('report-008', 'reference-integrity-check', 0.91),

    -- Report 9
    ('report-009', 'patient-count-check', 0.43),
    ('report-009', 'data-completeness-check', 0.56),
    ('report-009', 'format-validation-check', 0.61),
    ('report-009', 'coding-standard-check', 0.59),

    -- Report 10
    ('report-010', 'patient-count-check', 0.96),
    ('report-010', 'data-completeness-check', 0.94),
    ('report-010', 'date-consistency-check', 0.92),
    ('report-010', 'duplicate-detection-check', 0.98),
    ('report-010', 'format-validation-check', 0.93),
    ('report-010', 'reference-integrity-check', 0.95),
    ('report-010', 'outlier-detection-check', 0.78),
    ('report-010', 'coding-standard-check', 0.91);

-- Add some recent reports with current timestamps for immediate testing
INSERT INTO report (id, timestamp, agent_id) VALUES
    ('report-current-1', CURRENT_TIMESTAMP - INTERVAL '2' HOUR, 'agent-001'),
    ('report-current-2', CURRENT_TIMESTAMP - INTERVAL '1' HOUR, 'agent-002'),
    ('report-current-3', CURRENT_TIMESTAMP - INTERVAL '30' MINUTE, 'agent-004');

-- Add results for current reports
INSERT INTO quality_check_result (report_id, quality_check_hash, result) VALUES
    ('report-current-1', 'patient-count-check', 0.87),
    ('report-current-1', 'data-completeness-check', 0.91),
    ('report-current-1', 'duplicate-detection-check', 0.94),
    ('report-current-2', 'patient-count-check', 0.72),
    ('report-current-2', 'format-validation-check', 0.68),
    ('report-current-2', 'coding-standard-check', 0.75),
    ('report-current-3', 'patient-count-check', 0.95),
    ('report-current-3', 'data-completeness-check', 0.93),
    ('report-current-3', 'date-consistency-check', 0.89),
    ('report-current-3', 'reference-integrity-check', 0.92);
