#!/bin/bash

# Master script to set up and run the Employee Management System
# This script handles:
# 0. Environment configuration (.env setup)
# 1. MySQL server startup
# 2. Database connection verification
# 3. Database initialization
# 4. Maven build and application launch

set -e  # Exit on error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Get the project root directory
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$PROJECT_ROOT/.env"
ENV_EXAMPLE="$PROJECT_ROOT/.env.example"

# Function to print colored messages
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo ""
}

# Function to check if a command exists
command_exists() {
    command -v "$1" &> /dev/null
}

# Step 0: Environment Configuration
setup_environment() {
    print_header "Step 0: Environment Configuration"
    
    if [ -f "$ENV_FILE" ]; then
        print_info ".env file already exists."
        read -p "Do you want to reconfigure it? (y/N): " reconfigure
        if [[ ! "$reconfigure" =~ ^[Yy]$ ]]; then
            print_success "Using existing .env file."
            return 0
        fi
    fi
    
    # Check if .env.example exists
    if [ ! -f "$ENV_EXAMPLE" ]; then
        print_warning ".env.example not found. Creating default .env file..."
        cat > "$ENV_FILE" << 'EOF'
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=emp_mgmt
DB_USER=root
DB_PASS=
EOF
    else
        print_info "Copying .env.example to .env..."
        cp "$ENV_EXAMPLE" "$ENV_FILE"
    fi
    
    print_success ".env file created."
    echo ""
    print_info "Please configure your database credentials:"
    echo ""
    
    # Read current values from .env
    source "$ENV_FILE"
    
    # Prompt for DB_USER
    read -p "Enter MySQL username [${DB_USER:-root}]: " input_user
    DB_USER="${input_user:-${DB_USER:-root}}"
    
    # Prompt for DB_PASS (hidden input)
    read -s -p "Enter MySQL password (leave empty if none): " input_pass
    echo ""
    DB_PASS="${input_pass:-${DB_PASS:-}}"
    
    # Prompt for DB_HOST
    read -p "Enter MySQL host [${DB_HOST:-localhost}]: " input_host
    DB_HOST="${input_host:-${DB_HOST:-localhost}}"
    
    # Prompt for DB_PORT
    read -p "Enter MySQL port [${DB_PORT:-3306}]: " input_port
    DB_PORT="${input_port:-${DB_PORT:-3306}}"
    
    # Prompt for DB_NAME
    read -p "Enter database name [${DB_NAME:-emp_mgmt}]: " input_name
    DB_NAME="${input_name:-${DB_NAME:-emp_mgmt}}"
    
    # Write updated values to .env
    cat > "$ENV_FILE" << EOF
# Database Configuration
DB_HOST=$DB_HOST
DB_PORT=$DB_PORT
DB_NAME=$DB_NAME
DB_USER=$DB_USER
DB_PASS=$DB_PASS
EOF
    
    print_success "Database credentials configured in .env file."
    echo ""
}

# Step 1: Start MySQL Server
start_mysql() {
    print_header "Step 1: Starting MySQL Server"
    
    cd "$PROJECT_ROOT"
    
    if [ ! -f "src/db/start-mysql.sh" ]; then
        print_error "start-mysql.sh not found at src/db/start-mysql.sh"
        exit 1
    fi
    
    # Make sure the script is executable
    chmod +x src/db/start-mysql.sh
    
    print_info "Starting MySQL server..."
    if bash src/db/start-mysql.sh --init; then
        print_success "MySQL server is running and database initialized."
    else
        print_warning "MySQL startup script completed with warnings. Continuing..."
    fi
    echo ""
}

# Step 2: Verify Database Connection
verify_connection() {
    print_header "Step 2: Verifying Database Connection"
    
    cd "$PROJECT_ROOT"
    
    print_info "Testing database connection..."
    if mvn -q exec:java -Dexec.mainClass="com.employeemgmt.db.DatabaseConnectionManager" -Dexec.cleanupDaemonThreads=false; then
        print_success "Database connection verified successfully."
    else
        print_error "Failed to connect to database. Please check your credentials in .env file."
        exit 1
    fi
    echo ""
}

# Step 3: Initialize Database
initialize_database() {
    print_header "Step 3: Initializing Database"
    
    cd "$PROJECT_ROOT"
    
    print_info "Initializing database schema and sample data..."
    if mvn -q exec:java -Dexec.mainClass="com.employeemgmt.db.DatabaseInit" -Dexec.cleanupDaemonThreads=false; then
        print_success "Database initialized successfully."
    else
        print_warning "Database initialization completed with warnings. This may be normal if data already exists."
    fi
    echo ""
}

# Step 4: Build and Run Application
build_and_run() {
    print_header "Step 4: Building and Running Application"
    
    cd "$PROJECT_ROOT"
    
    # Check if Maven is installed
    if ! command_exists mvn; then
        print_error "Maven is not installed. Please install Maven first."
        print_info "On macOS: brew install maven"
        print_info "On Ubuntu: sudo apt install maven"
        exit 1
    fi
    
    print_info "Cleaning previous build..."
    mvn clean -q
    
    print_info "Compiling project..."
    if mvn compile -q; then
        print_success "Project compiled successfully."
    else
        print_error "Compilation failed. Please check the error messages above."
        exit 1
    fi
    
    echo ""
    print_success "All setup steps completed successfully!"
    echo ""
    print_info "Launching Employee Management System..."
    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}  Starting JavaFX Application...${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    
    # Run the JavaFX application
    mvn javafx:run
}

# Main execution flow
main() {
    clear
    echo -e "${GREEN}"
    echo "╔════════════════════════════════════════════════╗"
    echo "║   Employee Management System - Setup & Run     ║"
    echo "╚════════════════════════════════════════════════╝"
    echo -e "${NC}"
    
    # Check prerequisites
    print_info "Checking prerequisites..."
    
    if ! command_exists java; then
        print_error "Java is not installed. Please install Java 17 or higher."
        exit 1
    fi
    
    if ! command_exists mvn; then
        print_error "Maven is not installed. Please install Maven."
        exit 1
    fi
    
    if ! command_exists mysql; then
        print_error "MySQL client is not installed. Please install MySQL."
        exit 1
    fi
    
    print_success "All prerequisites are installed."
    echo ""
    
    # Execute setup steps
    setup_environment
    start_mysql
    
    # Small delay to ensure MySQL is fully ready
    sleep 2
    
    verify_connection
    initialize_database
    build_and_run
}

# Trap errors and provide helpful message
trap 'print_error "Script failed at line $LINENO. Please check the error message above."; exit 1' ERR

# Run main function
main "$@"

