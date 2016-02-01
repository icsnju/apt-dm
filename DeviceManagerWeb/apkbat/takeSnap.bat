@echo off
rem 获取当前运行设备
adb devices > devices.txt
rem 获取APK文件
 dir apk /B > apk.txt
rem 运行monkeyrunner 脚本
 monkeyrunner C:\Users\mcl\Desktop\myproject\work_thread.py