-- Create server table
CREATE TABLE server (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(500) NOT NULL,
    name VARCHAR(255),
    client_id VARCHAR(255),
    client_secret VARCHAR(500),
    status VARCHAR(50) NOT NULL
);

-- Create server_interaction table
CREATE TABLE server_interaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    server_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (server_id) REFERENCES server(id) ON DELETE CASCADE
);

-- Create index on server_id for better query performance
CREATE INDEX idx_server_interaction_server_id ON server_interaction(server_id);

-- Create index on timestamp for better query performance when sorting by time
CREATE INDEX idx_server_interaction_timestamp ON server_interaction(timestamp);

