# PowerShell script to check and install MySQL on Windows

#Requires -Version 5.1

$ErrorActionPreference = "Stop"

# Function to check if running as Administrator
function Test-Administrator {
    $currentUser = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($currentUser)
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

# Function to check if MySQL is installed
function Test-MySQLInstallation {
    echo "Checking MySQL Installation"
    
    # Check if mysql command is available
    $mysqlCommand = Get-Command mysql -ErrorAction SilentlyContinue
    
    if ($mysqlCommand) {
        try {
            $mysqlVersion = & mysql --version 2>&1
            echo "MySQL is installed: $mysqlVersion"
            
            # Check if MySQL service is running
            $service = Get-Service -Name "MySQL*" -ErrorAction SilentlyContinue | Select-Object -First 1
            
            if ($service) {
                if ($service.Status -eq "Running") {
                    echo "MySQL service '$($service.Name)' is running."
                    return 0
                }
                else {
                    echo "MySQL service '$($service.Name)' is installed but not running."
                    return 2
                }
            }
            else {
                echo "MySQL is installed but service not found."
                return 2
            }
        }
        catch {
            echo "MySQL command found but unable to verify installation."
            return 2
        }
    }
    else {
        echo "MySQL is not installed."
        return 1
    }
}

# Function to check if winget is available
function Test-Winget {
    try {
        $wingetVersion = & winget --version 2>&1
        echo "winget is available: $wingetVersion"
        return $true
    }
    catch {
        echo "winget is not available."
        return $false
    }
}

# Function to install MySQL using winget
function Install-MySQLWithWinget {
    echo "Installing MySQL with winget"
    
    if (-not (Test-Administrator)) {
        echo "Administrator privileges required to install MySQL."
        echo "Please run this script as Administrator."
        return $false
    }
    
    echo "Installing MySQL Community Server..."
    
    try {
        # Install Oracle MySQL
        & winget install Oracle.MySQL --silent --accept-package-agreements --accept-source-agreements
        
        if ($LASTEXITCODE -eq 0) {
            echo "MySQL installed successfully via winget!"
            return $true
        }
        else {
            echo "winget installation failed with exit code: $LASTEXITCODE"
            return $false
        }
    }
    catch {
        echo "Failed to install MySQL via winget: $_"
        return $false
    }
}

# Function to install MySQL manually
function Install-MySQLManually {
    echo "Manual MySQL Installation"
    
    echo "Please follow these steps to install MySQL manually:"
    echo ""
    echo "Option 1: MySQL Community Server (Recommended)"
    echo "  1. Visit: https://dev.mysql.com/downloads/mysql/"
    echo "  2. Select 'Windows (x86, 64-bit), MSI Installer'"
    echo "  3. Download the installer (mysql-installer-community)"
    echo "  4. Run the installer and choose 'Developer Default' or 'Server only'"
    echo "  5. Follow the configuration wizard"
    echo "  6. Set a root password when prompted"
    echo ""
    echo "Option 2: MySQL Installer for Windows"
    echo "  1. Visit: https://dev.mysql.com/downloads/installer/"
    echo "  2. Download the mysql-installer-web-community"
    echo "  3. Run the installer"
    echo ""
    
    $openBrowser = Read-Host "Would you like to open the MySQL download page in your browser? (y/N)"
    
    if ($openBrowser -match '^[Yy]$') {
        Start-Process "https://dev.mysql.com/downloads/mysql/"
        echo "Browser opened. Please download and install MySQL."
    }
    
    echo ""
    echo "After installation, please:"
    echo "  1. Make sure MySQL service is started"
    echo "  2. Note your root password"
    echo "  3. Add MySQL bin directory to PATH if not done automatically"
    echo "  4. Run this script again to verify the installation"
    echo ""
}

# Function to start MySQL service
function Start-MySQLService {
    echo "Starting MySQL Service"
    
    if (-not (Test-Administrator)) {
        echo "Administrator privileges required to start MySQL service."
        echo "Please run this script as Administrator."
        return $false
    }
    
    $service = Get-Service -Name "MySQL*" -ErrorAction SilentlyContinue | Select-Object -First 1
    
    if (-not $service) {
        echo "MySQL service not found."
        return $false
    }
    
    echo "Starting MySQL service '$($service.Name)'..."
    
    try {
        Start-Service $service.Name
        Start-Sleep -Seconds 3
        
        $service.Refresh()
        if ($service.Status -eq "Running") {
            echo "MySQL service is running!"
            return $true
        }
        else {
            echo "Failed to start MySQL service."
            return $false
        }
    }
    catch {
        echo "Error starting MySQL service: $_"
        return $false
    }
}

# Function to add MySQL to PATH
function Add-MySQLToPath {
    echo "Configuring MySQL PATH"
    
    # Common MySQL installation paths
    $possiblePaths = @(
        "C:\Program Files\MySQL\MySQL Server 8.0\bin",
        "C:\Program Files\MySQL\MySQL Server 8.1\bin",
        "C:\Program Files\MySQL\MySQL Server 8.2\bin",
        "C:\Program Files\MySQL\MySQL Server 8.3\bin",
        "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin"
    )
    
    $mysqlBinPath = $null
    foreach ($path in $possiblePaths) {
        if (Test-Path $path) {
            $mysqlBinPath = $path
            break
        }
    }
    
    if ($mysqlBinPath) {
        echo "Found MySQL bin directory at: $mysqlBinPath"
        
        $currentPath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
        
        if ($currentPath -notlike "*$mysqlBinPath*") {
            $addToPath = Read-Host "Would you like to add MySQL to system PATH? (y/N)"
            
            if ($addToPath -match '^[Yy]$') {
                if (-not (Test-Administrator)) {
                    echo "Administrator privileges required to modify system PATH."
                    echo "Please run this script as Administrator."
                    return
                }
                
                try {
                    $newPath = "$mysqlBinPath;$currentPath"
                    [System.Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
                    echo "MySQL added to system PATH"
                    echo "Please restart your terminal for changes to take effect."
                }
                catch {
                    echo "Failed to update PATH: $_"
                }
            }
        }
        else {
            echo "MySQL is already in system PATH"
        }
    }
    else {
        echo "Could not automatically locate MySQL bin directory."
        echo "Please add MySQL bin directory to PATH manually if needed."
    }
}

# Function to display connection info
function Show-ConnectionInfo {
    echo "MySQL Connection Information"
    
    echo "Host: localhost"
    echo "Port: 3306"
    echo "User: root"
    echo ""
    
    echo "To connect to MySQL, use:"
    echo "    mysql -u root -p"
    echo ""
    
    echo "To check MySQL service status:"
    echo "    Get-Service MySQL*"
    echo ""
    
    echo "To stop MySQL service (as Administrator):"
    echo "    Stop-Service MySQL80  # or your MySQL service name"
    echo ""
    
    echo "To start MySQL service (as Administrator):"
    echo "    Start-Service MySQL80  # or your MySQL service name"
    echo ""
}

# Main execution
function Main {
    echo "MySQL Installation Script for Windows"
    
    # Check if running on Windows
    if (-not $IsWindows -and $PSVersionTable.PSVersion.Major -lt 6) {
        # PowerShell 5.x on Windows doesn't have $IsWindows variable
        # Assume we're on Windows
    }
    elseif ($IsWindows -eq $false) {
        echo "This script is designed for Windows only."
        echo "Please use the appropriate script for your operating system."
        exit 1
    }
    
    # Check MySQL
    $mysqlStatus = Test-MySQLInstallation
    
    if ($mysqlStatus -eq 0) {
        echo ""
        echo "MySQL is already installed and running. No action needed."
        Show-ConnectionInfo
        exit 0
    }
    elseif ($mysqlStatus -eq 2) {
        # MySQL installed but not running
        echo ""
        $startConfirm = Read-Host "Would you like to start MySQL now? (y/N)"
        
        if ($startConfirm -match '^[Yy]$') {
            if (Start-MySQLService) {
                Show-ConnectionInfo
            }
        }
        exit 0
    }
    
    # Ask user if they want to install MySQL
    echo ""
    $installConfirm = Read-Host "Would you like to install MySQL now? (y/N)"
    
    if ($installConfirm -match '^[Yy]$') {
        # Check if winget is available
        if (Test-Winget) {
            echo ""
            $useWinget = Read-Host "Use winget for automatic installation? (Requires Administrator) (y/N)"
            
            if ($useWinget -match '^[Yy]$') {
                if (Install-MySQLWithWinget) {
                    echo ""
                    Add-MySQLToPath
                    
                    echo ""
                    Start-Sleep -Seconds 5
                    
                    Start-MySQLService
                    
                    echo ""
                    Show-ConnectionInfo
                    
                    echo ""
                    echo "MySQL installation and setup complete!"
                    echo ""
                    echo "Next steps:"
                    echo "  1. Set a root password using MySQL Workbench or command line"
                    echo "  2. Update your .env file with database credentials"
                    echo "  3. Run the application setup"
                    echo ""
                    exit 0
                }
                else {
                    echo "Automatic installation failed. Falling back to manual installation."
                    Install-MySQLManually
                }
            }
            else {
                Install-MySQLManually
            }
        }
        else {
            echo "winget is not available. Manual installation required."
            Install-MySQLManually
        }
    }
    else {
        echo "Installation cancelled."
        exit 1
    }
}

# Run main function
try {
    Main
}
catch {
    echo "Script failed: $_"
    exit 1
}
