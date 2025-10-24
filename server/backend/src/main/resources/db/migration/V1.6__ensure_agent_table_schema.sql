-- Create agent_interaction table
CREATE TABLE agent_interaction (
    id VARCHAR(36) NOT NULL PRIMARY KEY UNIQUE,
    timestamp TIMESTAMP NOT NULL,
    type VARCHAR(20) NOT NULL,
    agent_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_agent_interaction_agent FOREIGN KEY (agent_id) REFERENCES agent(id) ON DELETE CASCADE
);

-- Add index on agent_id for better query performance
CREATE INDEX idx_agent_interaction_agent_id ON agent_interaction(agent_id);

-- Add index on timestamp for sorting interactions
CREATE INDEX idx_agent_interaction_timestamp ON agent_interaction(timestamp);

-- Add index on type for filtering by interaction type
CREATE INDEX idx_agent_interaction_type ON agent_interaction(type);
