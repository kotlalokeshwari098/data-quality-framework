-- Create agent table
CREATE TABLE agent (
    id VARCHAR(36) NOT NULL PRIMARY KEY UNIQUE,
    status VARCHAR(20)
);

-- Add index on status for better query performance
CREATE INDEX idx_agent_status ON agent(status);
