-- Create report table
CREATE TABLE report (
    id VARCHAR(36) NOT NULL PRIMARY KEY UNIQUE,
    timestamp TIMESTAMP NOT NULL,
    agent_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_report_agent FOREIGN KEY (agent_id) REFERENCES agent(id)
);

-- Add index on agent_id for better query performance
CREATE INDEX idx_report_agent_id ON report(agent_id);

-- Add index on timestamp for better query performance when filtering by date
CREATE INDEX idx_report_timestamp ON report(timestamp);

