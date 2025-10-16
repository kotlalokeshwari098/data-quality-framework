CREATE TABLE settings (
    setting_name VARCHAR(100) PRIMARY KEY,
    setting_value VARCHAR(500) NOT NULL
);

INSERT INTO settings (setting_name, setting_value) VALUES ('fhirUrl', 'http://localhost:8080/fhir');
INSERT INTO settings (setting_name, setting_value) VALUES ('fhirUsername', 'fhiruser');
INSERT INTO settings (setting_name, setting_value) VALUES ('fhirPassword', 'fhirpass');
