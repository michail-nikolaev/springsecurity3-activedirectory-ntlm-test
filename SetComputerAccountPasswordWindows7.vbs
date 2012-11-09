' ===========================================================
' Copyright (c) 2012, Marcel Schoen, Switzerland
' This script is available under the LGPL license as part of
' the "ntlmv2-auth" project:
'
' https://sourceforge.net/p/ntlmv2auth/wiki/Home/
' 
' Usage:
' 
' C:> cscript SetComputerAccountPasswordWindows7.vbs <account DN> <password>
' 
' ===========================================================
'
Option Explicit

Dim strDn, objPassword, strPassword, objComputer

If WScript.arguments.count <> 2 Then
	WScript.Echo "Usage: cscript SetComputerAccountPassword.vbs <ComputerDN> <password>"
	WScript.Quit
End If

' Get the DN from the first commandline argument
strDn = WScript.arguments.item(0)
strPassword = WScript.arguments.item(1)


' Get the computer account object and set the new password for it
Set objComputer = GetObject("LDAP://" & strDn)
objComputer.SetPassword strPassword

WScript.Echo "Password successfully changed."
WScript.Quit
