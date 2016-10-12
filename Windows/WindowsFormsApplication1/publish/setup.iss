; Script generated by the Inno Script Studio Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "ClipboardHost"
#define MyAppVersion "1.7"
#define MyAppPublisher "Tomas Radvansky"
#define MyAppURL "https://github.com/radvansky-tomas"
#define MyAppExeName "ClipboardHost.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{20EB546F-7F09-42C3-A4F2-89BDE70539D0}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
OutputBaseFilename=setup
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked; OnlyBelowVersion: 0,6.1

[Files]
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\ClipboardHost.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\Camalot.Common.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\ClipboardHost.application"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\ClipboardHost.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\ClipboardHost.exe.config"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\ClipboardHost.exe.manifest"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\ClipboardHost.pdb"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\ClipboardHost.vshost.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\Managed.Adb.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\Managed.Adb.pdb"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\Managed.Adb.xml"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\Mono.Posix.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\MoreLinq.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\MoreLinq.xml"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\Newtonsoft.Json.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\Newtonsoft.Json.xml"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\System.Reactive.Core.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\System.Reactive.Core.xml"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\System.Reactive.Interfaces.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\System.Reactive.Interfaces.xml"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\System.Reactive.Linq.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\System.Reactive.Linq.xml"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\System.Reactive.PlatformServices.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\System.Reactive.PlatformServices.xml"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\System.Reactive.Windows.Threading.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\System.Reactive.Windows.Threading.xml"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\System.Runtime.InteropServices.RuntimeInformation.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\Zeroconf.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\Zeroconf.pdb"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Tomasko\Documents\Visual Studio 2015\Projects\WindowsFormsApplication1\WindowsFormsApplication1\bin\Release\Zeroconf.xml"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\bin\Release\adb\adb.exe"; DestDir: "{app}\adb"; Flags: onlyifdoesntexist
Source: "..\bin\Release\adb\AdbWinApi.dll"; DestDir: "{app}\adb"; Flags: onlyifdoesntexist
Source: "..\bin\Release\adb\AdbWinUsbApi.dll"; DestDir: "{app}\adb"; Flags: onlyifdoesntexist

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: quicklaunchicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

[Dirs]
Name: "{app}\adb"
