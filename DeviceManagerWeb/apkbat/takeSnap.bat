@echo off
rem ��ȡ��ǰ�����豸
adb devices > devices.txt
rem ��ȡAPK�ļ�
 dir apk /B > apk.txt
rem ����monkeyrunner �ű�
 monkeyrunner C:\Users\mcl\Desktop\myproject\work_thread.py