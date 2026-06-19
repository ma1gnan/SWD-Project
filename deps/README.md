# Dependencies Installation Scripts

This directory contains automated installation scripts for the Employee Management System dependencies, organized by operating system.

## Overview

The scripts check if required dependencies are installed and install them if needed:
- **Java 21** - Required for running the application
- **MySQL 8.0+** - Required for database operations

## Directory Structure

```
deps/
├── macos/          # Scripts for macOS
│   ├── install-java.sh
│   └── install-mysql.sh
├── windows/        # Scripts for Windows
│   ├── install-java.ps1
│   └── install-mysql.ps1
└── README.md       # This file
```

## Usage

### macOS

#### Install Java 21

```bash
cd deps/macos
chmod +x install-java.sh
./install-java.sh
```

**What it does:**
- Checks if Java is installed and verifies version
- Installs Homebrew if not present
- Installs OpenJDK 21 via Homebrew
- Configures `JAVA_HOME` and `PATH` environment variables
- Adds configuration to your shell profile (.zshrc or .bash_profile)

#### Install MySQL

```bash
cd deps/macos
chmod +x install-mysql.sh
./install-mysql.sh
```

**What it does:**
- Checks if MySQL is installed and running
- Installs Homebrew if not present
- Installs MySQL 8.0+ via Homebrew
- Starts MySQL service automatically
- Optionally sets a root password
- Provides connection information

### Windows

#### Install Java 21

**Option 1: Run from PowerShell (Recommended)**

```powershell
cd deps\windows
powershell -ExecutionPolicy Bypass -File .\install-java.ps1
```

**Option 2: Run from File Explorer**
1. Right-click on `install-java.ps1`
2. Select "Run with PowerShell"

**What it does:**
- Checks if Java is installed and verifies version
- Attempts automatic installation via winget (Windows Package Manager)
- Falls back to manual installation instructions if needed
- Configures `JAVA_HOME` and `PATH` environment variables
- Supports multiple JDK distributions (Microsoft OpenJDK, Oracle, Adoptium)

#### Install MySQL

**Option 1: Run from PowerShell (Recommended)**

```powershell
cd deps\windows
powershell -ExecutionPolicy Bypass -File .\install-mysql.ps1
```

**Option 2: Run from File Explorer**
1. Right-click on `install-mysql.ps1`
2. Select "Run with PowerShell"

**What it does:**
- Checks if MySQL is installed and running
- Attempts automatic installation via winget (requires Administrator)
- Falls back to manual installation instructions if needed
- Starts MySQL service
- Adds MySQL to system PATH
- Provides connection information

## Prerequisites

### macOS
- macOS 10.15 (Catalina) or higher
- Terminal access
- Internet connection
- Administrator privileges (for some operations)

### Windows
- Windows 10 or higher
- PowerShell 5.1 or higher
- Internet connection
- Administrator privileges (for some operations)
- **Optional but recommended:** Windows Package Manager (winget) - included in Windows 11 and Windows 10 (version 1809+)

## Troubleshooting

### macOS

**Issue: "Permission denied" error**
```bash
chmod +x install-java.sh
chmod +x install-mysql.sh
```

**Issue: Homebrew installation fails**
- Check internet connection
- Visit https://brew.sh for manual installation instructions

**Issue: MySQL won't start**
```bash
# Check MySQL status
brew services list | grep mysql

# Try restarting
brew services restart mysql

# Check logs
tail -f /opt/homebrew/var/mysql/*.err  # Apple Silicon
tail -f /usr/local/var/mysql/*.err     # Intel Mac
```

### Windows

**Issue: "Execution of scripts is disabled on this system"**
```powershell
# Run PowerShell as Administrator and execute:
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

**Issue: winget not found**
- Update Windows to the latest version
- Install App Installer from Microsoft Store
- Or use manual installation option

**Issue: MySQL service won't start**
```powershell
# Check service status
Get-Service MySQL*

# Try starting manually
Start-Service MySQL80  # or your MySQL service name

# Check event logs
Get-EventLog -LogName Application -Source MySQL -Newest 10
```

**Issue: "Administrator privileges required"**
- Right-click PowerShell and select "Run as Administrator"
- Or use manual installation option

## After Installation

### Verify Java Installation

**macOS/Linux:**
```bash
java -version
echo $JAVA_HOME
```

**Windows:**
```powershell
java -version
echo $env:JAVA_HOME
```

Expected output: Java version 21.x.x or higher

### Verify MySQL Installation

**macOS:**
```bash
mysql --version
brew services list | grep mysql
```

**Windows:**
```powershell
mysql --version
Get-Service MySQL*
```

Expected output: MySQL version 8.0.x or higher

### Test MySQL Connection

**All platforms:**
```bash
mysql -u root -p
```

Enter your root password when prompted.

## Next Steps

After installing dependencies:

1. **Configure database credentials**
   - Create/update `.env` file in project root
   - Set `DB_USER` and `DB_PASS`

2. **Run the application**
   ```bash
   # From project root
   ./run.sh
   ```

   Or manually:
   ```bash
   mvn clean javafx:run
   ```

## Manual Installation

If the automated scripts don't work for your system, refer to:

### Java 21
- **Microsoft OpenJDK**: https://learn.microsoft.com/en-us/java/openjdk/download
- **Oracle JDK**: https://www.oracle.com/java/technologies/downloads/#java21
- **Adoptium**: https://adoptium.net/temurin/releases/?version=21

### MySQL
- **MySQL Community Server**: https://dev.mysql.com/downloads/mysql/
- **MySQL Documentation**: https://dev.mysql.com/doc/
