-- Create agent table
CREATE TABLE agent (
    id VARCHAR(36) NOT NULL PRIMARY KEY UNIQUE,
    status VARCHAR(20)
);

-- Add index on status for better query performance
CREATE INDEX idx_agent_status ON agent(status);

-- Add agent_id column to user_account table with foreign key constraint
ALTER TABLE user_account ADD COLUMN agent_id VARCHAR(36);
ALTER TABLE user_account ADD CONSTRAINT fk_user_account_agent
    FOREIGN KEY (agent_id) REFERENCES agent(id);
