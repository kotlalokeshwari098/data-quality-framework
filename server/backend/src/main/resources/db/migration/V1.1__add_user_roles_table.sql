-- Create user_roles table to store user role assignments
-- This table links users to their roles via user_id and role enum value

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE
);

-- Create index for performance on user_id lookups
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);

-- Add some comments for clarity
COMMENT ON TABLE user_roles IS 'Stores role assignments for users - many-to-many relationship';
COMMENT ON COLUMN user_roles.user_id IS 'Foreign key reference to user_account.id';
COMMENT ON COLUMN user_roles.role IS 'Role enum value (HUMAN_USER, ADMIN)';

-- Assign both ADMIN and HUMAN_USER roles to the admin user
INSERT INTO user_roles (user_id, role)
SELECT id, role
FROM user_account
CROSS JOIN (VALUES ('ADMIN'), ('HUMAN_USER')) AS roles(role)
WHERE username = 'admin';
