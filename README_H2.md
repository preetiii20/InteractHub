# Running InteractHub backend services with H2 (local/in-memory)

This file documents how to start the backend microservices using an in-memory H2 database profile. I added `application-h2.properties` to each backend module so you can start services without MySQL.

How to use the H2 profile (PowerShell examples):

1) Start one service (example: chat)

```powershell
cd .\backend-microservices\chat
.\mvnw.cmd -Dspring.profiles.active=h2 spring-boot:run
```

2) Or pass H2 properties directly when running (no profile needed):

```powershell
cd .\backend-microservices\chat
.\mvnw.cmd spring-boot:run `
  '-Dspring.datasource.url=jdbc:h2:mem:interacthub;DB_CLOSE_DELAY=-1;MODE=MySQL' `
  '-Dspring.datasource.driver-class-name=org.h2.Driver' `
  '-Dspring.datasource.username=sa' `
  '-Dspring.datasource.password=' `
  '-Dspring.jpa.hibernate.ddl-auto=update'
```

3) H2 console
- When a service is running with the H2 profile, open the H2 console at http://localhost:<port>/h2-console
- JDBC URL: jdbc:h2:mem:interacthub
- User: sa
- Password: (leave empty)

Notes and troubleshooting
- I added `MODE=MySQL` to improve compatibility with the existing MySQL-based DDL. Some MySQL-specific DDL may still produce warnings in H2.
- If you see `Communications link failure` or `Connection refused` errors, it means the service attempted to reach MySQL (likely because a different active profile picked MySQL). Ensure you use `-Dspring.profiles.active=h2` or the `application-h2.properties` settings are picked.
- If ports conflict, verify no other process is listening on the same port (use `Get-Process` / `netstat -a -b` in PowerShell).

Next recommended steps (optional):
- Add `spring.profiles.active=h2` to a shell script or Windows `.ps1` for convenience.
- Add a Maven profile that sets `spring.profiles.active=h2` so you can run `mvn -P h2 spring-boot:run`.
