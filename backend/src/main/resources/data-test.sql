-- H2 Database initialization for test environment
-- This file runs after Hibernate creates the schema but before tests

-- Disable foreign key checks for H2 (H2 syntax)
SET REFERENTIAL_INTEGRITY FALSE;

-- Configure H2 to be more MySQL-compatible
SET MODE MySQL;

-- Simple test data insert to verify H2 is working
-- This will be executed only if the tables exist
