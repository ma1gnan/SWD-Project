#!/bin/bash

# Script to check and install MySQL on macOS

set -e

# Function to check if Homebrew is installed
check_homebrew() {
    if ! command -v brew &> /dev/null; then
        echo "Homebrew is not installed."
        echo "Homebrew is required to install MySQL on macOS."
        echo ""
        read -p "Would you like to install Homebrew now? (y/N): " install_brew
        
        if [[ "$install_brew" =~ ^[Yy]$ ]]; then
            echo "Installing Homebrew..."
            /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
            
            # Add Homebrew to PATH for Apple Silicon Macs
            if [[ $(uname -m) == 'arm64' ]]; then
                echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
                eval "$(/opt/homebrew/bin/brew shellenv)"
            fi
            
            echo "Homebrew installed successfully."
        else
            echo "Homebrew is required to proceed. Exiting."
            exit 1
        fi
    else
        echo "Homebrew is installed."
    fi
}

# Function to check if MySQL is installed
check_mysql() {
    echo "Checking MySQL Installation"
    
    if command -v mysql &> /dev/null; then
        MYSQL_VERSION=$(mysql --version 2>&1)
        echo "MySQL is installed: $MYSQL_VERSION"
        
        # Check if MySQL service is running
        if brew services list | grep mysql | grep started &> /dev/null; then
            echo "MySQL service is running."
        else
            echo "MySQL is installed but not running."
            return 2
        fi
        return 0
    else
        echo "MySQL is not installed."
        return 1
    fi
}

# Function to install MySQL
install_mysql() {
    echo "Installing MySQL"
    
    echo "Installing MySQL via Homebrew..."
    echo ""
    
    # Update Homebrew
    echo "Updating Homebrew..."
    brew update
    
    # Install MySQL
    echo "Installing mysql..."
    brew install mysql
    
    echo "MySQL installation completed!"
}

# Function to start MySQL service
start_mysql() {
    echo "Starting MySQL Service"
    
    echo "Starting MySQL service..."
    brew services start mysql
    
    # Wait for MySQL to be ready
    echo "Waiting for MySQL to be ready..."
    sleep 5
    
    # Check if MySQL is running
    for i in {1..30}; do
        if mysqladmin ping -h localhost --silent &> /dev/null; then
            echo "MySQL service is running!"
            return 0
        fi
        sleep 1
    done
    
    echo "MySQL may not be fully started yet. Please wait a moment and try connecting."
}

# Function to secure MySQL installation
secure_mysql() {
    echo "MySQL Security Setup"
    
    echo ""
    echo "By default, MySQL is installed with no root password."
    echo "It is recommended to set a root password for security."
    echo ""
    
    read -p "Would you like to set a root password now? (y/N): " set_password
    
    if [[ "$set_password" =~ ^[Yy]$ ]]; then
        echo ""
        read -s -p "Enter new root password: " root_password
        echo ""
        read -s -p "Confirm root password: " root_password_confirm
        echo ""
        
        if [ "$root_password" != "$root_password_confirm" ]; then
            echo "Passwords do not match. Skipping password setup."
            return 1
        fi
        
        if [ -z "$root_password" ]; then
            echo "Empty password provided. Skipping password setup."
            return 1
        fi
        
        echo "Setting root password..."
        mysql -u root <<EOF
ALTER USER 'root'@'localhost' IDENTIFIED BY '$root_password';
FLUSH PRIVILEGES;
EOF
        
        if [ $? -eq 0 ]; then
            echo "Root password set successfully!"
            echo ""
            echo "Please update your .env file with:"
            echo "    DB_USER=root"
            echo "    DB_PASS=$root_password"
        else
            echo "Failed to set root password."
        fi
    else
        echo "Skipping password setup."
        echo "MySQL root user has no password. Update .env file with:"
        echo "    DB_USER=root"
        echo "    DB_PASS="
    fi
}

# Function to display connection info
display_connection_info() {
    echo "MySQL Connection Information"
    
    echo "Host: localhost"
    echo "Port: 3306"
    echo "User: root"
    echo ""
    
    echo "To connect to MySQL, use:"
    echo "    mysql -u root -p"
    echo ""
    
    echo "To stop MySQL service:"
    echo "    brew services stop mysql"
    echo ""
    
    echo "To restart MySQL service:"
    echo "    brew services restart mysql"
    echo ""
    
    echo "To check MySQL service status:"
    echo "    brew services list | grep mysql"
}

# Main execution
main() {
    echo "MySQL Installation Script for macOS"
    
    # Check if running on macOS
    if [[ "$OSTYPE" != "darwin"* ]]; then
        echo "This script is designed for macOS only."
        echo "Please use the appropriate script for your operating system."
        exit 1
    fi
    
    # Check Homebrew
    check_homebrew
    
    # Check MySQL
    MYSQL_STATUS=0
    check_mysql
    MYSQL_STATUS=$?
    
    if [ $MYSQL_STATUS -eq 0 ]; then
        echo ""
        echo "MySQL is already installed and running. No action needed."
        display_connection_info
        exit 0
    elif [ $MYSQL_STATUS -eq 2 ]; then
        # MySQL installed but not running
        echo ""
        read -p "Would you like to start MySQL now? (y/N): " start_confirm
        
        if [[ "$start_confirm" =~ ^[Yy]$ ]]; then
            start_mysql
            display_connection_info
        fi
        exit 0
    fi
    
    # Ask user if they want to install MySQL
    echo ""
    read -p "Would you like to install MySQL now? (y/N): " install_confirm
    
    if [[ "$install_confirm" =~ ^[Yy]$ ]]; then
        install_mysql
        start_mysql
        
        echo ""
        secure_mysql
        
        echo ""
        display_connection_info
        
        echo ""
        echo "MySQL installation and setup complete!"
        echo ""
        echo "Next steps:"
        echo "  1. Update your .env file with database credentials"
        echo "  2. Run the application setup: ./run.sh"
    else
        echo "Installation cancelled."
        exit 1
    fi
}

# Run main function
main "$@"
