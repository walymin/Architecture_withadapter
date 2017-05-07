@echo on
@echo --- build common
gradlew -p %1 -Pmarket=config/markets.txt apkRelease
@echo off
