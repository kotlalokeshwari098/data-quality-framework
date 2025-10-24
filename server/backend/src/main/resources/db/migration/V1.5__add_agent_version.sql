-- Add version column to agent table
ALTER TABLE agent ADD COLUMN version VARCHAR(255) DEFAULT 'Unknown';

