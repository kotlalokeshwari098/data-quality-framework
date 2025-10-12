-- Create agent table
CREATE TABLE agent (
    id VARCHAR(36) NOT NULL PRIMARY KEY UNIQUE,
    name VARCHAR(255),
    status VARCHAR(20)
);

-- Add index on status for better query performance
CREATE INDEX idx_agent_status ON agent(status);

-- Add agent_id column to user_account table with foreign key constraint
ALTER TABLE user_account ADD COLUMN agent_id VARCHAR(36);
ALTER TABLE user_account ADD CONSTRAINT fk_user_account_agent
    FOREIGN KEY (agent_id) REFERENCES agent(id);
delete from user_account where ID = 102;
INSERT INTO user_account (username, password) VALUES
    ('admin', '$argon2id$v=19$m=19456,t=2,p=1$SQGK8wXpQw5b+qjuq/Ih1A$WP87YsUIErq6O+7rMk65U0cH4OHBRdrnM3yIG50gwpE');
-- Assign both ADMIN and HUMAN_USER roles to the admin user
INSERT INTO user_roles (user_id, role)
SELECT id, role
FROM user_account
         CROSS JOIN (VALUES ('ADMIN'), ('HUMAN_USER')) AS roles(role)
WHERE username = 'admin';