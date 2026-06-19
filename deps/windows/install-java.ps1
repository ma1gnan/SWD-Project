# PowerShell script to check and install Java 21 on Windows

#Requires -Version 5.1

$ErrorActionPreference = "Stop"

# Function to check if running as Administrator
function Test-Administrator {
    $currentUser = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($currentUser)
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

# Function to get Java version
function Get-JavaVersion {
    try {
        $javaOutput = & java -version 2>&1
        if ($javaOutput -match 'version "(\d+)') {
            return [int]$matches[1]
        }
        return 0
    }
    catch {
        return 0
    }
}

# Function to check if Java is installed
function Test-JavaInstallation {
    echo "Checking Java Installation"
    
    $javaCommand = Get-Command java -ErrorAction SilentlyContinue
    
    if ($javaCommand) {
        $javaVersion = Get-JavaVersion
        $fullVersion = & java -version 2>&1 | Select-Object -First 1
        
        echo "Java is installed: $fullVersion"
        
        if ($javaVersion -ge 21) {
            echo "Java version $javaVersion meets the requirement (Java 21+)."
            
            # Get JAVA_HOME
            $javaHome = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")
            if ($javaHome) {
                echo "JAVA_HOME: $javaHome"
            }
            else {
                echo "JAVA_HOME environment variable is not set."
            }
            
            return $true
        }
        else {
            echo "Java version $javaVersion is installed, but Java 21 or higher is required."
            return $false
        }
    }
    else {
        echo "Java is not installed."
        return $false
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

# Function to install Java using winget
function Install-JavaWithWinget {
    echo "Installing Java 21 with winget"
    
    echo "Installing Microsoft OpenJDK 21..."
    
    try {
        # Install Microsoft OpenJDK 21
        & winget install Microsoft.OpenJDK.21 --silent --accept-package-agreements --accept-source-agreements
        
        if ($LASTEXITCODE -eq 0) {
            echo "Java 21 installed successfully via winget!"
            return $true
        }
        else {
            echo "winget installation failed with exit code: $LASTEXITCODE"
            return $false
        }
    }
    catch {
        echo "Failed to install Java via winget: $_"
        return $false
    }
}

# Function to install Java manually
function Install-JavaManually {
    echo "Manual Java 21 Installation"
    
    echo "Please follow these steps to install OpenJDK 21 manually:"
    echo ""
    echo "  1. Visit: https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-21"
    echo "  2. Download the MSI installer for Windows x64"
    echo "  3. Run the installer and follow the prompts"
    echo ""
    
    $openBrowser = Read-Host "Would you like to open the Microsoft OpenJDK download page in your browser? (y/N)"
    
    if ($openBrowser -match '^[Yy]$') {
        Start-Process "https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-21"
        echo "Browser opened. Please download and install Java 21."
    }
    
    echo ""
    echo "After installation, please:"
    echo "  1. Close and reopen this PowerShell window"
    echo "  2. Run this script again to verify the installation"
    echo ""
}

# Function to set JAVA_HOME
function Set-JavaHome {
    echo "Configuring JAVA_HOME"
    
    # Try to find Java installation
    $possiblePaths = @(
        "C:\Program Files\Microsoft\jdk-21*",
        "C:\Program Files\Java\jdk-21*"
    )
    
    $javaPath = $null
    foreach ($path in $possiblePaths) {
        $found = Get-Item $path -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found) {
            $javaPath = $found.FullName
            break
        }
    }
    
    if ($javaPath) {
        echo "Found Java installation at: $javaPath"
        
        $setEnv = Read-Host "Would you like to set JAVA_HOME to this location? (y/N)"
        
        if ($setEnv -match '^[Yy]$') {
            if (-not (Test-Administrator)) {
                echo "Administrator privileges required to set system environment variables."
                echo "Please run this script as Administrator, or set JAVA_HOME manually."
                return
            }
            
            try {
                [System.Environment]::SetEnvironmentVariable("JAVA_HOME", $javaPath, "Machine")
                
                # Add to PATH
                $currentPath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
                $javaBinPath = Join-Path $javaPath "bin"
                
                if ($currentPath -notlike "*$javaBinPath*") {
                    $newPath = "$javaBinPath;$currentPath"
                    [System.Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
                    echo "Added Java to system PATH"
                }
                
                echo "JAVA_HOME set to: $javaPath"
                echo "Please restart your terminal for changes to take effect."
            }
            catch {
                echo "Failed to set environment variables: $_"
            }
        }
    }
    else {
        echo "Could not automatically locate Java installation."
        echo "Please set JAVA_HOME manually in System Environment Variables."
    }
}

# Main execution
function Main {
    echo "Java 21 Installation Script for Windows"
    
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
    
    # Check Java
    if (Test-JavaInstallation) {
        echo ""
        echo "Java 21+ is already installed. No action needed."
        exit 0
    }
    
    # Ask user if they want to install Java
    echo ""
    $installConfirm = Read-Host "Would you like to install Java 21 now? (y/N)"
    
    if ($installConfirm -match '^[Yy]$') {
        # Check if winget is available
        if (Test-Winget) {
            echo ""
            $useWinget = Read-Host "Use winget for automatic installation? (y/N)"
            
            if ($useWinget -match '^[Yy]$') {
                if (Install-JavaWithWinget) {
                    echo ""
                    Set-JavaHome
                    
                    echo ""
                    echo "Installation complete!"
                    echo ""
                    echo "IMPORTANT: Please restart your terminal and run:"
                    echo "    java -version"
                    echo ""
                    exit 0
                }
                else {
                    echo "Automatic installation failed. Falling back to manual installation."
                    Install-JavaManually
                }
            }
            else {
                Install-JavaManually
            }
        }
        else {
            echo "winget is not available. Manual installation required."
            Install-JavaManually
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
