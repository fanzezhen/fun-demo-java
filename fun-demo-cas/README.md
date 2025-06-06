# cas

jdk-11

java.exe -Dmaven.multiModuleProjectDirectory=D:\code\fanzezhen\demo\cas -Djansi.passthrough=true "-Dmaven.home=D:\software\apache-maven-3.6.3" "-Dclassworlds.conf=D:\software\apache-maven-3.6.3\bin\m2.conf" "-Dmaven.ext.class.path=D:\software\apache-maven-3.6.3\lib\maven-event-listener.jar" "-javaagent:D:\program files\JetBrains\IntelliJ IDEA 2023.1.2\lib\idea_rt.jar=58714:D:\program files\JetBrains\IntelliJ IDEA 2023.1.2\bin" -Dfile.encoding=UTF-8 -classpath "D:\program files\JetBrains\IntelliJ IDEA 2023.1.2\plugins\maven\lib\maven3\boot\plexus-classworlds-2.6.0.jar;D:\program files\JetBrains\IntelliJ IDEA 2023.1.2\plugins\maven\lib\maven3\boot\plexus-classworlds.license" org.codehaus.classworlds.Launcher -Didea.version=2023.1.2 -DskipTests=true clean install -pl cas

验证码推荐使用 Hutool-Captcha