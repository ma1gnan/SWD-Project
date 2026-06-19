#!/bin/bash

# Script to start MySQL server using configuration from .env file
# Usage: 
#   ./start-mysql.sh           - Start MySQL server
#   ./start-mysql.sh --init    - Start MySQL and initialize database with schema and sample data

# Get the project root directory (parent of src)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
ENV_FILE="$PROJECT_ROOT/.env"

# Default values (matching DatabaseConnectionManager defaults)
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-emp_mgmt}"
DB_USER="${DB_USER:-root}"
DB_PASS="${DB_PASS:-}"

# Function to load environment variables from .env file
load_env_file() {
    if [ -f "$ENV_FILE" ]; then
        echo "Loading configuration from .env file..."
        # Read .env file and export variables
        while IFS='=' read -r key value || [ -n "$key" ]; do
            # Skip empty lines and comments
            [[ -z "$key" || "$key" =~ ^[[:space:]]*# ]] && continue
            
            # Remove leading/trailing whitespace
            key=$(echo "$key" | xargs)
            value=$(echo "$value" | xargs)
            
            # Skip if key or value is empty
            [[ -z "$key" || -z "$value" ]] && continue
            
            # Export the variable
            export "$key=$value"
        done < "$ENV_FILE"
    else
        echo "Warning: .env file not found at $ENV_FILE"
        echo "Using default values..."
    fi
    
    # Override with .env values if they exist
    if [ -f "$ENV_FILE" ]; then
        source <(grep -v '^#' "$ENV_FILE" | grep -v '^$' | sed 's/^/export /')
    fi
}

# Function to check if MySQL is already running
check_mysql_running() {
    if command -v mysqladmin &> /dev/null; then
        mysqladmin ping -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" ${DB_PASS:+-p"$DB_PASS"} &> /dev/null
        return $?
    fi
    return 1
}

# Function to start MySQL using Homebrew services
start_with_brew() {
    if command -v brew &> /dev/null && brew services list | grep -q mysql; then
        echo "Starting MySQL using Homebrew services..."
        brew services start mysql
        return $?
    fi
    return 1
}

# Function to start MySQL using mysql.server
start_with_mysql_server() {
    if [ -f /usr/local/mysql/support-files/mysql.server ]; then
        echo "Starting MySQL using mysql.server..."
        sudo /usr/local/mysql/support-files/mysql.server start
        return $?
    elif [ -f /opt/homebrew/opt/mysql/support-files/mysql.server ]; then
        echo "Starting MySQL using mysql.server (Homebrew)..."
        /opt/homebrew/opt/mysql/support-files/mysql.server start
        return $?
    fi
    return 1
}

# Function to start MySQL using mysqld_safe
start_with_mysqld_safe() {
    if command -v mysqld_safe &> /dev/null; then
        echo "Starting MySQL using mysqld_safe..."
        mysqld_safe --user=mysql &
        return $?
    fi
    return 1
}

# Function to initialize database
initialize_database() {
    echo ""
    echo "========================================="
    echo "Initializing Database"
    echo "========================================="
    echo ""
    
    # Check if database exists, create if not
    echo "Checking if database '$DB_NAME' exists..."
    if mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" ${DB_PASS:+-p"$DB_PASS"} -e "USE $DB_NAME;" &> /dev/null; then
        echo "✓ Database '$DB_NAME' exists."
    else
        echo "Creating database '$DB_NAME'..."
        if mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" ${DB_PASS:+-p"$DB_PASS"} -e "CREATE DATABASE IF NOT EXISTS $DB_NAME;" &> /dev/null; then
            echo "✓ Database '$DB_NAME' created successfully."
        else
            echo "✗ Failed to create database '$DB_NAME'."
            exit 1
        fi
    fi
    
    # Execute schema file
    SCHEMA_FILE="$SCRIPT_DIR/schema.sql"
    if [ -f "$SCHEMA_FILE" ]; then
        echo ""
        echo "Executing schema file..."
        if mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" ${DB_PASS:+-p"$DB_PASS"} "$DB_NAME" < "$SCHEMA_FILE" 2>&1 | grep -v "Using a password on the command line"; then
            echo "✓ Schema executed successfully."
        else
            echo "✗ Failed to execute schema."
            exit 1
        fi
    else
        echo "✗ Schema file not found: $SCHEMA_FILE"
        exit 1
    fi
    
    # Execute sample data file
    SAMPLE_DATA_FILE="$SCRIPT_DIR/sample-data.sql"
    if [ -f "$SAMPLE_DATA_FILE" ]; then
        echo ""
        echo "Loading sample data..."
        if mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" ${DB_PASS:+-p"$DB_PASS"} "$DB_NAME" < "$SAMPLE_DATA_FILE" 2>&1 | grep -v "Using a password on the command line"; then
            echo "✓ Sample data loaded successfully."
        else
            echo "✗ Failed to load sample data."
            exit 1
        fi
    else
        echo "✗ Sample data file not found: $SAMPLE_DATA_FILE"
        exit 1
    fi
    
    echo ""
    echo "========================================="
    echo "✓ Database initialization complete!"
    echo "========================================="
    echo ""
}

# Main execution
main() {
    # Check for --init flag
    INIT_DB=false
    if [ "$1" = "--init" ]; then
        INIT_DB=true
    fi
    
    echo "========================================="
    echo "MySQL Server Startup Script"
    echo "========================================="
    echo ""
    
    # Load environment variables
    load_env_file
    
    # Display configuration
    echo "Configuration:"
    echo "  Host: $DB_HOST"
    echo "  Port: $DB_PORT"
    echo "  Database: $DB_NAME"
    echo "  User: $DB_USER"
    echo "  Password: ${DB_PASS:+***}"
    echo ""
    
    # Check if MySQL is already running
    echo "Checking if MySQL is already running..."
    if check_mysql_running; then
        echo "✓ MySQL server is already running on $DB_HOST:$DB_PORT"
        echo ""
        echo "Testing connection..."
        if mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" ${DB_PASS:+-p"$DB_PASS"} -e "SELECT 1;" &> /dev/null; then
            echo "✓ Connection successful!"
            
            # Initialize database if --init flag was provided
            if [ "$INIT_DB" = true ]; then
                initialize_database
            fi
        else
            echo "✗ Connection failed. Please check your credentials."
            exit 1
        fi
        exit 0
    else
        echo "MySQL server is not running. Attempting to start..."
        echo ""
    fi
    
    # Try different methods to start MySQL
    echo "Attempting to start MySQL server..."
    echo ""
    
    # Method 1: Homebrew services
    if start_with_brew; then
        echo ""
        echo "Waiting for MySQL to be ready..."
        sleep 3
        
        # Wait up to 30 seconds for MySQL to be ready
        for i in {1..30}; do
            if check_mysql_running; then
                echo "✓ MySQL server started successfully!"
                echo ""
                echo "Server is running on $DB_HOST:$DB_PORT"
                
                # Initialize database if --init flag was provided
                if [ "$INIT_DB" = true ]; then
                    initialize_database
                fi
                exit 0
            fi
            sleep 1
        done
    fi
    
    # Method 2: mysql.server
    if start_with_mysql_server; then
        echo ""
        echo "Waiting for MySQL to be ready..."
        sleep 3
        
        # Wait up to 30 seconds for MySQL to be ready
        for i in {1..30}; do
            if check_mysql_running; then
                echo "✓ MySQL server started successfully!"
                echo ""
                echo "Server is running on $DB_HOST:$DB_PORT"
                
                # Initialize database if --init flag was provided
                if [ "$INIT_DB" = true ]; then
                    initialize_database
                fi
                exit 0
            fi
            sleep 1
        done
    fi
    
    # Method 3: mysqld_safe (fallback)
    if start_with_mysqld_safe; then
        echo ""
        echo "Waiting for MySQL to be ready..."
        sleep 5
        
        # Wait up to 30 seconds for MySQL to be ready
        for i in {1..30}; do
            if check_mysql_running; then
                echo "✓ MySQL server started successfully!"
                echo ""
                echo "Server is running on $DB_HOST:$DB_PORT"
                
                # Initialize database if --init flag was provided
                if [ "$INIT_DB" = true ]; then
                    initialize_database
                fi
                exit 0
            fi
            sleep 1
        done
    fi
    
    # If we get here, MySQL couldn't be started
    echo ""
    echo "✗ Failed to start MySQL server."
    echo ""
    echo "Please try one of the following:"
    echo "  1. Start MySQL manually: brew services start mysql"
    echo "  2. Start MySQL manually: sudo /usr/local/mysql/support-files/mysql.server start"
    echo "  3. Check if MySQL is installed and configured correctly"
    echo ""
    echo "You can also check MySQL status with:"
    echo "  mysqladmin ping -h $DB_HOST -P $DB_PORT -u $DB_USER"
    exit 1
}

# Run main function
main "$@"

