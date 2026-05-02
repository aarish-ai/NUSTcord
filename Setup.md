# What you need before starting

You need three things installed and working: **Java JDK**, **Tomcat 9**, and **Maven**. Maven’s docs say Maven requires Java and that `mvn.cmd` must be on your PATH. Tomcat’s docs say its startup scripts use `JAVA_HOME` and `CATALINA_HOME`. ([Apache Maven][2])

---

# 1) Install and set up Tomcat 9

## Step 1. Download Tomcat 9

Download the **Tomcat 9 binary distribution** from the official Apache Tomcat site. Tomcat 9 is the right branch for a `javax.servlet` project like yours. ([Apache Tomcat][3])

## Step 2. Extract it

Unzip Tomcat into a simple folder, for example:

```text
C:\Program Files\Apache Software Foundation\Tomcat 9.0
```

Tomcat’s docs refer to the installation folder as the Tomcat home/base directory used by the startup scripts. ([Apache Tomcat][1])

## Step 3. Set `JAVA_HOME`

Set `JAVA_HOME` to your JDK folder, for example:

```text
C:\Program Files\Java\jdk-21
```

Tomcat’s setup docs say `JAVA_HOME` should point to the base path of the JDK before running the scripts. ([Apache Tomcat][3])

## Step 4. Set `CATALINA_HOME`

Set `CATALINA_HOME` to the Tomcat installation folder, for example:

```text
C:\Program Files\Apache Software Foundation\Tomcat 9.0
```

Tomcat’s startup docs say `CATALINA_HOME` is used by the startup scripts and should point to the Tomcat install directory. ([Apache Tomcat][1])

## Step 5. Check the variables

Open a **new** Command Prompt or PowerShell and run:

```powershell
java -version
```

If Java works, Tomcat can use it. Then later you will also test Maven with `mvn -v`. Maven’s Windows docs say the Maven `bin` folder must be in PATH, just like Java commands. ([Apache Maven][2])

## Step 6. Start Tomcat

Go to Tomcat’s `bin` folder and run:

```bat
startup.bat
```

That is the correct Windows startup script. Tomcat’s official docs list `startup.bat` for Windows and `startup.sh` for Unix-like systems. ([Apache Tomcat][1])

## Step 7. Confirm Tomcat is running

Open:

```text
http://localhost:8080/
```

If Tomcat is running, you should see the default Tomcat page. ([Apache Tomcat][1])

---

# 2) Install and set up Maven

## Step 8. Download Maven

Download the **binary ZIP** from the official Apache Maven download page. Maven’s install guide says to download the binary archive, not the source archive, for normal use. ([Apache Maven][4])

## Step 9. Extract Maven

Unzip Maven into a simple folder, for example:

```text
C:\Maven\apache-maven-3.9.15
```

Maven’s install docs say to extract the archive into any directory. ([Apache Maven][4])

## Step 10. Add Maven to PATH

Add Maven’s `bin` folder to your PATH:

```text
C:\Maven\apache-maven-3.9.15\bin
```

Maven’s Windows docs say `mvn.cmd` is in the Maven `bin` directory and that directory must be in PATH. ([Apache Maven][5])

## Step 11. Test Maven

Open a **new** terminal and run:

```powershell
mvn -v
```

Maven’s docs say this should print the installed Maven version if setup is correct. ([Apache Maven][2])

If `mvn` is still not recognized, then Maven is not on PATH yet, or the terminal was not reopened after updating PATH. Maven’s Windows guide specifically points to PATH as the fix. ([Apache Maven][5])

---

# 3) Open the NUSTcord project and build it

## Step 12. Open the correct folder in IntelliJ IDEA

Open the **main project folder** — the folder that contains `pom.xml`. That is the Maven project root. Maven commands are run from the folder containing the POM. ([Apache Maven][6])

## Step 13. Check the project structure

You should see things like:

* `pom.xml`
* `src/main/java`
* `src/main/webapp`

That is the normal structure for a Maven Java web app. Maven’s WAR packaging is used for web applications. ([Apache Tomcat][7])

## Step 14. Open terminal in the project root

Open the terminal inside the **NUSTcord folder**, the one with `pom.xml`.

## Step 15. Run the build command

Run:

```bash
mvn clean package
```

Maven’s docs describe `mvn` as the command-line tool, and `clean package` is the normal build flow to produce the packaged output. ([Apache Maven][6])

## Step 16. Wait for the build to finish

If it succeeds, Maven will create a `target/` folder. For a web app, that usually includes a `.war` file. Maven’s WAR plugin documentation says the WAR plugin packages web applications into deployable WARs. ([Apache Tomcat][7])

---

# 4) Prepare the WAR file for Tomcat

## Step 17. Find the WAR in `target`

After the build, look inside `target/`. You should see something like:

```text
NUSTcord-1.0-SNAPSHOT.war
```

That is the file Tomcat will deploy. Tomcat’s deployment docs say a WAR dropped into `webapps/` is deployed automatically. ([Apache Tomcat][7])

## Step 18. Rename it to something simple

Rename the WAR to:

```text
NUSTcord.war
```

This is not strictly required, but it makes the URL much easier. Tomcat’s manager docs say the context path is derived from the WAR file name when you do not specify a custom path. ([Apache Tomcat][8])

## Step 19. About the extracted folder

You do **not** need to manually rename the extracted folder if Tomcat creates it after deployment. Tomcat handles WAR deployment automatically when the file is placed in `webapps/`. If you manually deploy an exploded app folder, then the folder name also becomes the context path. ([Apache Tomcat][7])

---

# 5) Deploy the WAR to Tomcat

## Step 20. Copy the WAR into `webapps`

Go to the Tomcat installation folder, then:

```text
Tomcat folder -> webapps
```

Paste:

```text
NUSTcord.war
```

Tomcat’s deployment docs say a web application can be deployed by placing the WAR into the server’s deployment area, including the `webapps` folder. ([Apache Tomcat][7])

## Step 21. Remove old copies if needed

If you previously deployed:

* `NUSTcord-1.0-SNAPSHOT.war`
* `NUSTcord.war`

keep only **one** WAR file in `webapps/`. Tomcat uses the WAR file name for the default context path, so duplicate versions can confuse the URL and the deployed app. ([Apache Tomcat][8])

---

# 6) Start Tomcat again

## Step 22. Run `startup.bat`

Go back to Tomcat’s `bin` folder and run:

```bat
startup.bat
```

On Windows, that is the correct command. `startup.sh` is for Linux/macOS, not Windows. ([Apache Tomcat][1])

## Step 23. Keep the window open

Leave the Tomcat console running. If you close it, the server stops.

## Step 24. Watch the logs

Tomcat writes startup and deployment information to its logs. If your app fails later, the logs are where you look first. ([Apache Tomcat][9])

---

# 7) Open the app in the browser

## Step 25. Use HTTP, not HTTPS

Open:

```text
http://localhost:8080/NUSTcord/
```

not `https://...`. Local Tomcat on port 8080 normally starts with HTTP unless you configure SSL/TLS yourself. Tomcat’s SSL docs show HTTPS requires separate SSL/TLS configuration. ([Apache Tomcat][10])

## Step 26. Use the right context path

If your WAR is named `NUSTcord.war`, then the context path is usually `/NUSTcord`. If the WAR is `NUSTcord-1.0-SNAPSHOT.war`, then the context path is usually `/NUSTcord-1.0-SNAPSHOT`. Tomcat’s deployment docs say the path is derived from the WAR name when no explicit path is set. ([Apache Tomcat][8])

## Step 27. Open your first page

If your app has `login.jsp` at the web root, open:

```text
http://localhost:8080/NUSTcord/login.jsp
```

If you later add an `index.jsp` or welcome file, you can make the app open automatically at `/NUSTcord/`. Tomcat’s deployment docs explain that the deployed app is then served under its context path. ([Apache Tomcat][7])

---

# 8) If something goes wrong

## If `mvn` is not recognized

Maven is not on PATH yet. Reopen the terminal after editing PATH and try again. Maven’s Windows guide says the Maven `bin` folder must be on PATH. ([Apache Maven][5])

## If Tomcat does not start

Check that:

* `JAVA_HOME` is correct
* `CATALINA_HOME` is correct
* port 8080 is free
* you used `startup.bat` on Windows ([Apache Tomcat][1])

## If the Tomcat page opens but NUSTcord does not

That usually means:

* the WAR was not deployed into `webapps/`
* or the URL is wrong
* or the WAR file name does not match the context path you are using ([Apache Tomcat][7])

## If the app deploys but shows an error

Open the Tomcat logs. Tomcat’s docs say logs are where startup and deployment problems are reported. ([Apache Tomcat][9])

---

# The full flow in one line

**Install Java → install Tomcat 9 → set `JAVA_HOME` and `CATALINA_HOME` → install Maven → add Maven `bin` to PATH → open NUSTcord root folder in IntelliJ → run `mvn clean package` → rename the WAR to `NUSTcord.war` → copy it into Tomcat `webapps/` → run `startup.bat` → open `http://localhost:8080/NUSTcord/login.jsp`.** ([Apache Tomcat][3])

Your earlier draft was close, but the two main corrections are important: use **`startup.bat`** on Windows, and use **`http://localhost:8080/...`**, not `https://`. Tomcat’s docs are very clear on both. ([Apache Tomcat][1])
