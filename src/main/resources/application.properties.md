spring.application.name=petopia
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://zenpetsql-zenpet.k.aivencloud.com:27145/petshop?createDatabaseIfNotExist=true&useSSL=false
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate Properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# File upload properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Firebase
firebase.service-account.file-path=src/main/resources/serviceAccountKey.json
firebase.service-account.path=classpath:serviceAccountKey.json
firebase.storage.bucket-name=
firebase.storage.image-pet=image/pets
firebase.storage.image-product=image/products
firebase.database.url=

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# JWT
petopia.app.jwtSecret=
petopia.app.jwtExpirationMs=

# NGROK
ngrok.authtoken=
ngrok.domain=