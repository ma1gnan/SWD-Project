#!/bin/bash

# Script to check and install Java 21 on macOS

set -e

# Function to check if Homebrew is installed
check_homebrew() {
    if ! command -v brew &> /dev/null; then
        echo "Homebrew is not installed."
        echo "Homebrew is required to install Java on macOS."
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

# Function to get Java version
get_java_version() {
    if command -v java &> /dev/null; then
        java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1
    else
        echo "0"
    fi
}

# Function to check if Java is installed and get version
check_java() {
    echo "Checking Java Installation"
    
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(get_java_version)
        FULL_VERSION=$(java -version 2>&1 | head -n 1)
        
        echo "Java is installed: $FULL_VERSION"
        
        if [ "$JAVA_VERSION" -ge 21 ]; then
            echo "Java version $JAVA_VERSION meets the requirement (Java 21+)."
            echo ""
            echo "Java Home: $(java -XshowSettings:properties -version 2>&1 | grep 'java.home' | awk '{print $3}')"
            return 0
        else
            echo "Java version $JAVA_VERSION is installed, but Java 21 or higher is required."
            return 1
        fi
    else
        echo "Java is not installed."
        return 1
    fi
}

# Function to install Java 21
install_java() {
    echo "Installing Java 21"
    
    echo "Installing OpenJDK 21 via Homebrew..."
    echo ""
    
    # Update Homebrew
    echo "Updating Homebrew..."
    brew update
    
    # Install OpenJDK 21
    echo "Installing openjdk@21..."
    brew install openjdk@21
    
    # Create symlink for system Java wrappers
    echo "Creating system symlinks..."
    if [ -d "/opt/homebrew/opt/openjdk@21" ]; then
        # Apple Silicon path
        sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk
    elif [ -d "/usr/local/opt/openjdk@21" ]; then
        # Intel Mac path
        sudo ln -sfn /usr/local/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk
    fi
    
    # Add to PATH
    echo "Configuring PATH..."
    
    SHELL_CONFIG=""
    if [ -f "$HOME/.zshrc" ]; then
        SHELL_CONFIG="$HOME/.zshrc"
    elif [ -f "$HOME/.bash_profile" ]; then
        SHELL_CONFIG="$HOME/.bash_profile"
    elif [ -f "$HOME/.bashrc" ]; then
        SHELL_CONFIG="$HOME/.bashrc"
    fi
    
    if [ -n "$SHELL_CONFIG" ]; then
        # Check if Java path is already in config
        if ! grep -q "openjdk@21" "$SHELL_CONFIG"; then
            echo "" >> "$SHELL_CONFIG"
            echo "# Java 21 (OpenJDK)" >> "$SHELL_CONFIG"
            
            if [ -d "/opt/homebrew/opt/openjdk@21" ]; then
                echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> "$SHELL_CONFIG"
                echo 'export JAVA_HOME="/opt/homebrew/opt/openjdk@21"' >> "$SHELL_CONFIG"
                export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"
                export JAVA_HOME="/opt/homebrew/opt/openjdk@21"
            elif [ -d "/usr/local/opt/openjdk@21" ]; then
                echo 'export PATH="/usr/local/opt/openjdk@21/bin:$PATH"' >> "$SHELL_CONFIG"
                echo 'export JAVA_HOME="/usr/local/opt/openjdk@21"' >> "$SHELL_CONFIG"
                export PATH="/usr/local/opt/openjdk@21/bin:$PATH"
                export JAVA_HOME="/usr/local/opt/openjdk@21"
            fi
            
            echo "Java 21 path added to $SHELL_CONFIG"
        fi
    fi
    
    echo ""
    echo "Java 21 installation completed!"
    echo ""
    
    # Verify installation
    if command -v java &> /dev/null; then
        FULL_VERSION=$(java -version 2>&1 | head -n 1)
        echo "Verification: $FULL_VERSION"
        echo "Java Home: $JAVA_HOME"
    else
        echo "Java command not found in current shell. Please restart your terminal."
    fi
}

# Main execution
main() {
    echo "Java 21 Installation Script for macOS"
    
    # Check if running on macOS
    if [[ "$OSTYPE" != "darwin"* ]]; then
        echo "This script is designed for macOS only."
        echo "Please use the appropriate script for your operating system."
        exit 1
    fi
    
    # Check Homebrew
    check_homebrew
    
    # Check Java
    if check_java; then
        echo ""
        echo "Java 21+ is already installed. No action needed."
        exit 0
    fi
    
    # Ask user if they want to install/upgrade Java
    echo ""
    read -p "Would you like to install Java 21 now? (y/N): " install_confirm
    
    if [[ "$install_confirm" =~ ^[Yy]$ ]]; then
        install_java
        
        echo ""
        echo "Installation complete!"
        echo ""
        echo "IMPORTANT: Please restart your terminal or run:"
        echo ""
        if [ -f "$HOME/.zshrc" ]; then
            echo "    source ~/.zshrc"
        elif [ -f "$HOME/.bash_profile" ]; then
            echo "    source ~/.bash_profile"
        fi
        echo ""
        echo "Then verify the installation with: java -version"
    else
        echo "Installation cancelled."
        exit 1
    fi
}

# Run main function
main "$@"
